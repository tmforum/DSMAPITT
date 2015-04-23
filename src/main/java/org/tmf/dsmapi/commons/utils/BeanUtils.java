/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.commons.utils;

import java.util.Iterator;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.codehaus.jackson.JsonNode;

/**
 *
 * @author maig7313
 */
public class BeanUtils {

    private static final PropertyUtilsBean PUB = new PropertyUtilsBean();

    /**
     *
     * @param bean
     * @param name
     * @return
     */
    public static Object getNestedProperty(Object bean, String name) {
        try {
            return PUB.getNestedProperty(bean, name);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param bean
     * @param name
     * @param value
     */
    public static void setNestedProperty(Object bean, String name, Object value) {
        try {
            PUB.setNestedProperty(bean, name, value);
        } catch (Exception ex) {
        }
    }
    
    /**
     *
     * @param bean
     * @param patchBean
     * @param node
     */
    public static void patch(Object bean, Object patch, JsonNode node) {
        String name;
        JsonNode child;
        Object value;
        Object patchValue;
        Iterator<String> it = node.getFieldNames();
        while (it.hasNext()) {
            name = it.next();
            patchValue = BeanUtils.getNestedProperty(patch, name);
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
