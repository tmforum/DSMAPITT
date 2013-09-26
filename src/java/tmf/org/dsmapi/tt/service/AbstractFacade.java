/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MultivaluedMap;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.ExceptionType;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import static tmf.org.dsmapi.tt.service.mapper.FacadeRestUtil.QUERY_KEY_FIELD;
import static tmf.org.dsmapi.tt.service.mapper.FacadeRestUtil.QUERY_KEY_FIELD_ESCAPE;
import tmf.org.dsmapi.tt.Status;
import tmf.org.dsmapi.tt.TroubleTicket;

/**
 * xxxxx
 *
 * @author pierregauthier
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public int create(List<T> entities) throws BadUsageException {
        for (T entity : entities) {
            this.create(entity);
        }
        return entities.size();
    }

    public void create(T entity) throws BadUsageException {
        getEntityManager().persist(entity);
    }

    public T edit(String id, T entity) throws UnknownResourceException {
        T targetEntity = this.find(id);
        if (targetEntity == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }
        getEntityManager().merge(entity);
        return entity;
    }

    public void remove(Object id) throws UnknownResourceException {
        T entity = getEntityManager().find(entityClass, id);
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) throws UnknownResourceException {
        T entity = getEntityManager().find(entityClass, id);
        if (entity == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }
        return entity;
    }

    public void detach(T entity) {
        getEntityManager().detach(entity);
    }

    public void invalidCache() {
        getEntityManager().clear();
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public List<T> findByCriteria(MultivaluedMap<String, String> queryParameters, Class<T> clazz) {
        List<T> resultsList = null;
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = criteriaBuilder.createQuery(clazz);
        List<Predicate> andPredicates = new ArrayList<Predicate>();
        Root<T> tt = cq.from(clazz);
        for (Map.Entry<String, List<String>> entry : queryParameters.entrySet()) {
            List<String> valueList = entry.getValue();
            Predicate predicate = null;
            if (valueList.size() > 1) {
                // name=value1&name=value&...&name=valueN
                // value of name is list [value1, value, ..., valueN]
                // => name=value1 OR name=value OR ... OR name=valueN
                List<Predicate> orPredicates = new ArrayList<Predicate>();
                for (String currentValue : valueList) {
                    Predicate orPredicate = buildPredicate(tt, entry.getKey(), currentValue);
                    orPredicates.add(orPredicate);
                }
                predicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            } else {
                // name=value
                // value of name is one element list [value]
                // => name=value
                predicate = buildPredicate(tt, entry.getKey(), valueList.get(0));
            }
            andPredicates.add(predicate);
        }
        cq.where(andPredicates.toArray(new Predicate[andPredicates.size()]));
        cq.select(tt);
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        resultsList = q.getResultList();
        return resultsList;
    }

    private Predicate buildPredicate(Path<T> tt, String name, String value) {
        Predicate predicate = null;
        int index = name.indexOf('.');
        if (index > 0 && index < name.length()) {
            // nested format  : rootFieldName.subFieldName=value
            String rootFieldName = name.substring(0, index);
            String subFieldName = name.substring(index + 1);
            Path<T> root = tt.get(rootFieldName);
            predicate = buildPredicate(root, subFieldName, value);
        } else {
            // simple format : name=value
            predicate = buildSimplePredicate(tt, name, value);
        }
        return predicate;
    }

    private Predicate buildSimplePredicate(Path<T> path, String name, String value) {
        Predicate predicate;
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        if (isMultipleOrValue(value)) {
            // name=value1,value,...,valueN
            // => name=value1 OR name=value OR ... OR name=valueN
            List<String> valueList = convertMultipleOrValueToList(value);
            List<Predicate> orPredicates = new ArrayList<Predicate>();
            for (String currentValue : valueList) {
                Predicate orPredicate = buildPredicateWithOperator(path, name, currentValue);
                orPredicates.add(orPredicate);
            }
            predicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
        } else if (isMultipleAndValue(value)) {
            // name=(subname1=value1&subname2=value&...&subnameN=valueN) 
            // => name.subname1=value1 AND name.subname2=value AND ... AND name.subnameN=valueN
            List<Map.Entry<String, String>> subFieldNameValue = convertMultipleAndValue(value);
            List<Predicate> andPredicates = new ArrayList<Predicate>();
            Path<T> root = path.get(name);
            for (Map.Entry<String, String> entry : subFieldNameValue) {
                String currentsubFieldName = entry.getKey();
                String currentValue = entry.getValue();
                Predicate andPredicate = buildPredicate(root, currentsubFieldName, currentValue);
                andPredicates.add(andPredicate);
            }
            predicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
        } else {
            // name=value
            predicate = buildPredicateWithOperator(path, name, value);
        }
        return predicate;
    }

    // value has format value1,value,...,valueN
    private static boolean isMultipleOrValue(String value) {
        return (value.indexOf(",") > -1);
    }

    // value has format (value1&value&...&valueN)
    private static boolean isMultipleAndValue(String value) {
        return (value.startsWith("(") && value.endsWith(")"));
    }

    // convert String "value1,value,...,valueN" 
    // to List [value1, value, ..., valueN]
    private static List<String> convertMultipleOrValueToList(String value) {
        List<String> valueList = new ArrayList<String>();
        String[] tokenArray = value.split(",");
        valueList.addAll(Arrays.asList(tokenArray));
        return valueList;
    }

    // convert String "(name1=value1&name2=value&...&nameN=valueN)" 
    // to List of Entry [name1=value1, name2=value, ..., nameN=valueN]
    // Conversion is not to a Map since there can be a same name with differents values
    private static List<Map.Entry<String, String>> convertMultipleAndValue(String multipleValue) {
        List<Map.Entry<String, String>> nameValueList = new ArrayList<Map.Entry<String, String>>();
        if (multipleValue.startsWith("(") && multipleValue.endsWith(")")) {
            String[] tokenArray = multipleValue.substring(1, multipleValue.length() - 1).split("&");
            for (String nameValue : tokenArray) {
                String[] split = nameValue.split("=");
                if (split.length == 2) {
                    String name = split[0];
                    String value = split[1];

                    nameValueList.add(new AbstractMap.SimpleEntry<String, String>(name, value));
                }
            }
        }
        return nameValueList;
    }

    protected Predicate buildPredicateWithOperator(Path<T> path, String name, String value) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        Operator operator = Operator.fromString(name);
        // perform operation, default operation is equal
        if (operator == null) {
            Path<T> attribute = path.get(name);
            Object valueObject = convertStringValueToObject(attribute, value);
            return criteriaBuilder.equal(attribute, valueObject);
        } else {
            switch (operator) {
                case GT:
                    return criteriaBuilder.greaterThan((Expression) path, value);
                case GTE:
                    return criteriaBuilder.greaterThanOrEqualTo((Expression) path, value);
                case LT:
                    return criteriaBuilder.lessThan((Expression) path, value);
                case LTE:
                    return criteriaBuilder.lessThanOrEqualTo((Expression) path, value);
                case NE: {
                    Object valueObject = convertStringValueToObject(path, value);
                    return criteriaBuilder.notEqual(path, valueObject);
                }
                case EQ: {
                    Object valueObject = convertStringValueToObject(path, value);
                    return criteriaBuilder.equal(path, valueObject);
                }
                case EX:
                    return criteriaBuilder.like((Expression) path, value.replace('*', '%'));
                default: {
                    Path<T> attribute = path.get(name);
                    Object valueObject = convertStringValueToObject(attribute, value);
                    return criteriaBuilder.equal(attribute, valueObject);
                }
            }
        }

    }

    private Enum safeEnumValueOf(Class enumType, String name) {
        Enum enumValue = null;
        if (name != null) {
            try {
                enumValue = Enum.valueOf(enumType, name);
            } catch (Exception e) {
                enumValue = null;
            }
        }
        return enumValue;
    }

    private Object convertStringValueToObject(Path<T> tt, String value) {
        Class javaType = tt.getJavaType();
        if (javaType.isEnum()) {
            Enum enumValue = safeEnumValueOf(javaType, value);
            return enumValue;
        } else {
            return value;
        }
    }

    //protected abstract Predicate buildPredicateForEnum(Path<T> path, String name, String value);
    enum Operator {

        EQ("eq"),
        GT("gt"),
        GTE("gte"),
        LT("lt"),
        LTE("lte"),
        NE("ne"),
        EX("ex");
        private String value;

        Operator(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public static Operator fromString(String value) {
            if (value != null) {
                for (Operator b : Operator.values()) {
                    if (value.equalsIgnoreCase(b.value)) {
                        return b;
                    }
                }
            }
            return null;
        }
    }
}
