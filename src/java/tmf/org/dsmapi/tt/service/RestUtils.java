/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.service;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import tmf.org.dsmapi.commons.utils.BeanUtils;

/**
 *
 * @author jyus7291
 */
public class RestUtils {

    /**
     *
     */
    public static final String ALL_FIELDS = "all";
    /**
     *
     */
    public static final String ID_FIELD = "id";
    private static final String QUERY_KEY_FIELD = "fields";
    private static final String QUERY_KEY_FIELD_ESCAPE = ":";

    /**
     *
     * @param bean
     * @param names 
     * @return
     */
    public static ObjectNode createNodeViewWithFields(Object bean, Set<String> names) {
        ObjectMapper mapper = new ObjectMapper();
        return createNodeViewWithFields(mapper, bean, names);
    }

    private static ObjectNode createNodeViewWithFields(ObjectMapper mapper, Object bean, Set<String> names) {
        // split names in 2 categories : 
        // simpleFields for simple value names with no '.'
        // nestedFields for nested value names with a '.'
        Set<String> simpleFields = new HashSet<String>();
        MultivaluedMapImpl nestedFields = new MultivaluedMapImpl();
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
                // current node is an array
                if (nestedBean.getClass().isArray()) {
                    Object[] array = (Object[]) nestedBean;
                    if (array.length > 0) {
                        // create a node for each element in array 
                        // and add created node in an arrayNode
                        Collection<JsonNode> nodes = new LinkedList<JsonNode>();
                        for (Object object : array) {
                            ObjectNode nestedNode = createNodeViewWithFields(mapper, object, nestedFieldNames);
                            nodes.add(nestedNode);
                        }
                        ArrayNode arrayNode = mapper.createArrayNode();
                        arrayNode.addAll(nodes);
                        rootNode.put(rootFieldName, arrayNode);
                    }
                } else {
                    // create recursively a node and add it in current root node
                    ObjectNode nestedNode = createNodeViewWithFields(mapper, nestedBean, nestedFieldNames);
                    rootNode.put(rootFieldName, nestedNode);
                }
            }
        }
        return rootNode;
    }

    // create a simple flat node with only one-level fields
    private static ObjectNode createNodeWithSimpleFields(ObjectMapper mapper, Object bean, Set<String> names) {
        ObjectNode node = mapper.createObjectNode();
        for (String name : names) {
            Object value = BeanUtils.getNestedProperty(bean, name);
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

    /**
     *
     * @param queryParameters
     * @return
     */
    public static Set<String> getFieldSet(MultivaluedMap<String, String> queryParameters) {
        Set<String> fieldSet = new HashSet<String>();
        if (queryParameters != null) {
            List<String> queryParameterField = queryParameters.remove(QUERY_KEY_FIELD_ESCAPE + QUERY_KEY_FIELD);
            if (queryParameterField == null) {
                queryParameterField = queryParameters.remove(QUERY_KEY_FIELD);
            }
            if (queryParameterField != null && !queryParameterField.isEmpty()) {
                String queryParameterValue = queryParameterField.get(0);
                if (queryParameterValue != null) {
                    String[] tokenArray = queryParameterValue.split(",");
                    fieldSet.addAll(Arrays.asList(tokenArray));
                }
            }
        }
        return fieldSet;
    }

    /**
     *
     * @param bean
     * @param patchBean
     * @param node
     */
    public static void patch(Object bean, Object patchBean, JsonNode node) {
        String name;
        JsonNode child;
        Object value;
        Object patchValue;
        Iterator<String> it = node.getFieldNames();
        while (it.hasNext()) {
            name = it.next();
            patchValue = BeanUtils.getNestedProperty(patchBean, name);
            child = node.get(name);
            if (child.isObject()) {
                value = BeanUtils.getNestedProperty(bean, name);
                if (value != null) {
                    patch(value, patchValue, child);
                    BeanUtils.setNestedProperty(bean, name, value);
                } else {
                    BeanUtils.setNestedProperty(bean, name, patchValue);
                }
            } else {
                BeanUtils.setNestedProperty(bean, name, patchValue);
            }
        }
    }
}
