package org.tmf.dsmapi.commons.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author maig7313
 */
public class URIParser {

    public static final String ALL_FIELDS = "all";
    public static final String ID_FIELD = "id";
    private static final String QUERY_KEY_FIELD = "fields";
    private static final String QUERY_KEY_FIELD_ESCAPE = ":";

    private URIParser() {
    }

    /**
     *
     * @param queryParameters
     * @return
     */
//    public static Set<String> getFieldsSelection(MultivaluedMap<String, String> queryParameters) {
    public static Set<String> getFieldsSelection(Map<String, List<String>> queryParameters) {
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

    public static MultivaluedMap<String, String> getParameters(UriInfo info) {
        if (info != null) {
            URI requestUri = info.getRequestUri();
            if (requestUri != null) {
                String rawQuery = requestUri.getRawQuery();
                return getParameters(rawQuery);
            }
        }
        return new MultivaluedHashMap();
    }

    public static MultivaluedMap<String, String> getParameters(String rawQuery) {
        MultivaluedMap<String, String> map = new MultivaluedHashMap();
        //return info.getQueryParameters();
        try {
            if (rawQuery == null || rawQuery.length() == 0) {
                return map;
            }
            String query = URLDecoder.decode(rawQuery, "UTF-8");
            if (query == null || query.length() == 0) {
                return map;
            }

            StringBuffer attribute = new StringBuffer();
            StringBuffer value = new StringBuffer();
            boolean inAttr = true;
            boolean inValue = false;
            int brackets = 0;
            for (int i = 0; i < query.length(); i++) {
                char c = query.charAt(i);
                switch (c) {
                    case '&':
                        if (brackets <= 0) {
                            brackets = 0;
                            if (attribute.length() > 0) {
                                map.putSingle(attribute.toString(), value.toString());
                            }
                            attribute = new StringBuffer();
                            value = new StringBuffer();
                            inAttr = true;
                            inValue = false;
                        } else {
                            if (inValue) {
                                value.append(c);
                            } else if (inAttr) {
                                attribute.append(c);
                            }
                        }
                        break;
                    case '=':
                        if (brackets <= 0) {
                            brackets = 0;
                            inValue = true;
                            inAttr = false;
                            value = new StringBuffer();
                        } else {
                            if (inValue) {
                                value.append(c);
                            } else if (inAttr) {
                                attribute.append(c);
                            }
                        }
                        break;
                    case '(':
                        if (inValue) {
                            brackets++;
                            value.append(c);
                        } else if (inAttr) {
                            attribute.append(c);
                        }
                        break;
                    case ')':
                        if (inValue) {
                            brackets--;
                            value.append(c);
                        } else if (inAttr) {
                            attribute.append(c);
                        }
                        break;

                    default:
                        if (inValue) {
                            value.append(c);
                        } else if (inAttr) {
                            attribute.append(c);
                        }
                        break;
                }
            }
            if (attribute.length() > 0) {
                map.putSingle(attribute.toString(), value.toString());
            }

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(URIParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return map;

    }
}
