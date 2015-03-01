package org.tmf.dsmapi.commons.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class Jackson {

    private Jackson() {
    }

    public static ObjectNode createNode(Object bean, Set<String> fieldNames) {
        ObjectMapper mapper = new ObjectMapper();
        return createNode(mapper, bean, fieldNames);
    }

    public static List<ObjectNode> createNodes(Collection list, Set<String> fieldNames) {
        List<ObjectNode> nodeList = new ArrayList<ObjectNode>();
        for (Object element : list) {
            ObjectNode node = createNode(element, fieldNames);
            nodeList.add(node);
        }
        return nodeList;
    }

    public static Object refineBean(Object bean, Set<String> fieldNames) {
        ObjectNode rootNode = Jackson.createNode(bean, fieldNames);
        Object result = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);  
            result = mapper.treeToValue(rootNode, bean.getClass());
        } catch (IOException ex) {
            Logger.getLogger(Jackson.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private static ObjectNode createNode(ObjectMapper mapper, Object bean, Set<String> names) {
        // split fieldNames in 2 categories : 
        // simpleFields for simple property names with no '.'
        // nestedFields for nested property names with a '.'
        Set<String> simpleFields = new HashSet<String>();
        MultivaluedMap nestedFields = new MultivaluedHashMap();
        for (String name : names) {
            int index = name.indexOf('.');
            boolean isNestedField = index > 0 && index < name.length();
            if (isNestedField) {
                String rootFieldName = name.substring(0, index);
                String subFieldName = name.substring(index + 1);
                nestedFields.add(rootFieldName, subFieldName);
            } else {
                simpleFields.add(name);
            }
        }

        // create a simple node with only one level containing all simples properties
        ObjectNode rootNode = createNodeWithSimpleFields(mapper, bean, simpleFields);

        // create nested nodes with deeper levels
        Set<Map.Entry<String, List<String>>> entrySet = nestedFields.entrySet();
        // for each nested value, create recursively a node        
        for (Map.Entry<String, List<String>> entry : entrySet) {
            String rootFieldName = entry.getKey();
            // add in current node only if full value is not already present in 1st level
            if (!simpleFields.contains(rootFieldName)) {
                Object nestedBean = BeanUtils.getNestedProperty(bean, rootFieldName);
                // add only non null fields
                if (nestedBean == null) {
                    break;
                }
                Set<String> nestedFieldNames = new HashSet<String>(entry.getValue());
                // current node is an array or a list
                if ((nestedBean.getClass().isArray()) || (Collection.class.isAssignableFrom(nestedBean.getClass()))) {
                    Object[] array = null;
                    if ((nestedBean.getClass().isArray())) {
                        array = (Object[]) nestedBean;
                    } else {
                        Collection collection = (Collection) nestedBean;
                        array = collection.toArray();
                    }
                    if (array.length > 0) {
                        // create a node for each element in array 
                        // and add created node in an arrayNode
                        Collection<JsonNode> nodes = new LinkedList<JsonNode>();
                        for (Object object : array) {
                            ObjectNode nestedNode = createNode(mapper, object, nestedFieldNames);
                            if (nestedNode != null && nestedNode.size() > 0) {
                                nodes.add(nestedNode);
                            }
                        }
                        ArrayNode arrayNode = mapper.createArrayNode();
                        arrayNode.addAll(nodes);
                        if (arrayNode.size() > 0) {
                            rootNode.put(rootFieldName, arrayNode);
                        }
                    }
                } else {
                    // create recursively a node and add it in current root node
                    ObjectNode nestedNode = createNode(mapper, nestedBean, nestedFieldNames);
                    if (nestedNode != null && nestedNode.size() > 0) {
                        rootNode.put(rootFieldName, nestedNode);
                    }
                }
            }
        }
        return rootNode;
    }

    // create a simple flat node with only one-level fields
    private static ObjectNode createNodeWithSimpleFields(ObjectMapper mapper, Object bean, Set<String> names) {
        ObjectNode node = mapper.createObjectNode();
        Object value;
        for (String name : names) {
            value = BeanUtils.getNestedProperty(bean, name);
            if (value != null) {
                nodePut(node, name, value);
            }
        }
        return node;
    }

    // generic node.put for any Object
    private static void nodePut(ObjectNode node, String name, Object value) {
        if (value instanceof Boolean) {
            node.put(name, (Boolean) value);
        } else if (value instanceof Integer) {
            node.put(name, (Integer) value);
        } else if (value instanceof Long) {
            node.put(name, (Long) value);
        } else if (value instanceof Float) {
            node.put(name, (Float) value);
        } else if (value instanceof Double) {
            node.put(name, (Double) value);
        } else if (value instanceof BigDecimal) {
            node.put(name, (BigDecimal) value);
        } else if (value instanceof String) {
            node.put(name, (String) value);
        } else {
            node.putPOJO(name, value);
        }
    }
}