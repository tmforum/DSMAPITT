package org.tmf.dsmapi.commons.facade;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MultivaluedMap;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.ExceptionType;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.utils.TMFDate;

/**
 *
 * @author pierregauthier
 */
public abstract class AbstractFacade<T> {


    private Class<T> entityClass;
    private static final String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static SimpleDateFormat formatter = new SimpleDateFormat(pattern);

    /**
     *
     * @param entityClass
     */
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     *
     * @return
     */
    protected abstract EntityManager getEntityManager();

    /**
     *
     * @param entities
     * @return
     * @throws BadUsageException
     */
    public int create(List<T> entities) throws BadUsageException {
        for (T entity : entities) {
            this.create(entity);
        }
        return entities.size();
    }

    /**
     *
     * @param entity
     * @throws BadUsageException
     */
    public void create(T entity) throws BadUsageException {
        getEntityManager().persist(entity);
    }

    /**
     *
     * @param entity
     * @return
     * @throws UnknownResourceException
     */
    public T edit(T entity) throws UnknownResourceException {
        getEntityManager().merge(entity);
        return entity;
    }

    /**
     *
     * @param id
     * @throws UnknownResourceException
     */
    public void remove(Object id) throws UnknownResourceException {
        T entity = getEntityManager().find(entityClass, id);
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public void removeAll() {
        List<T> all = findAll();
        EntityManager em = getEntityManager();
        for (T entity : all) {
            em.remove(entity);
        }
    }

    /**
     *
     * @param id
     * @return
     * @throws UnknownResourceException
     */
    public T find(Object id) throws UnknownResourceException {
        T entity = getEntityManager().find(entityClass, id);
        if (entity == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }
        return entity;
    }

    /**
     *
     * @param entity
     */
    public void detach(T entity) {
        getEntityManager().detach(entity);
    }

    /**
     *
     */
    public void clearCache() {
        getEntityManager().clear();
    }

    /**
     *
     * @return
     */
    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     *
     * @param range
     * @return
     */
    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    /**
     *
     * @return
     */
    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     *
     * @param map
     * @param clazz
     * @return
     */
//    public List<T> findByCriteria(MultivaluedMap<String, String> map, Class<T> clazz) {
    public List<T> findByCriteria(Map<String, List<String>> map, Class<T> clazz) {
        List<T> resultsList = null;
        try {
            CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<T> cq = criteriaBuilder.createQuery(clazz);
            List<Predicate> andPredicates = new ArrayList<Predicate>();
            Root<T> tt = cq.from(clazz);
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                List<String> valueList = entry.getValue();
                Predicate predicate = null;
                if (valueList.size() > 1) {
                    // name=value1&name=value2&...&name=valueN
                    // value of name is list [value1, value2, ..., valueN]
                    // => name=value1 OR name=value2 OR ... OR name=valueN
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
        } catch (Exception ex) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.INFO, "findByCriteria error, null result returned", ex);
            return null;
        }
    }

    private Predicate buildPredicate(Path<T> tt, String name, String value) throws BadUsageException {
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

    private Predicate buildSimplePredicate(Path<T> tt, String name, String value) throws BadUsageException {
        Predicate predicate;
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        if (isMultipleAndValue(value)) {
            // name=(subname1=value1&subname2=value2&...&subnameN=valueN) 
            // => name.subname1=value1 AND name.subname2=value2 AND ... AND name.subnameN=valueN
            List<Map.Entry<String, String>> subFieldNameValue = convertMultipleAndValue(value);
            List<Predicate> andPredicates = new ArrayList<Predicate>();
            Path<T> root = tt.get(name);
            for (Map.Entry<String, String> entry : subFieldNameValue) {
                String currentsubFieldName = entry.getKey();
                String currentValue = entry.getValue();
                Predicate andPredicate = buildPredicate(root, currentsubFieldName, currentValue);
                andPredicates.add(andPredicate);
            }
            predicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
        } else if (isMultipleOrValue(value)) {
            // name=value1,value2,...,valueN
            // => name=value1 OR name=value2 OR ... OR name=valueN
            List<String> valueList = convertMultipleOrValueToList(value);
            List<Predicate> orPredicates = new ArrayList<Predicate>();
            for (String currentValue : valueList) {
                Predicate orPredicate = buildPredicateWithOperator(tt, name, currentValue);
                orPredicates.add(orPredicate);
            }
            predicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
        } else {
            // name=value
            predicate = buildPredicateWithOperator(tt, name, value);
        }
        return predicate;
    }

    // value has format value1,value2,...,valueN
    private static boolean isMultipleOrValue(String value) {
        return (value.indexOf(",") > -1);
    }

    // value has format (value1&value2&...&valueN)
    private static boolean isMultipleAndValue(String value) {
        return (value.startsWith("(") && value.endsWith(")"));
    }

    // convert String "value1,value2,...,valueN" 
    // to List [value1, value2, ..., valueN]
    private static List<String> convertMultipleOrValueToList(String value) {
        List<String> valueList = new ArrayList<String>();
        String[] tokenArray = value.split(",");
        valueList.addAll(Arrays.asList(tokenArray));
        return valueList;
    }

    // convert String "(name1=value1&name2=value2&...&nameN=valueN)" 
    // to List of Entry [name1=value1, name2=value2, ..., nameN=valueN]
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

    // safe Enum.valueOf without exception 
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

    private Object convertStringValueToObject(String value, Class clazz) throws BadUsageException {
        Object convertedValue = null;
        if (clazz.isEnum()) {
            convertedValue = safeEnumValueOf(clazz, value);
        } else if (Date.class.isAssignableFrom(clazz)) {
            convertedValue = TMFDate.parse(value);
        } else if ((clazz.isPrimitive() && !clazz.equals(boolean.class))
                || (Number.class.isAssignableFrom(clazz))) {
            try {
                convertedValue = NumberFormat.getInstance().parse(value);
            } catch (ParseException ex) {
                convertedValue = null;
            }
        } else {
            convertedValue = value;
        }
        if (convertedValue != null) {
            return convertedValue;
        } else {
            throw new BadUsageException(ExceptionType.BAD_USAGE_FORMAT, "Wrong format for value " + value);
        }

    }

    // operators = and <> are compatibles with all types
    // operators < > <= >= are compatibles with numbers and dates
    // operator "LIKE" is compatible with String
    private boolean classCompatibleWithOperator(Class clazz, Operator operator) {
        if (operator == null) {
            return true;
        } else {
            switch (operator) {
                case NE:
                case EQ:
                    return true;
                case GT:
                case GTE:
                case LT:
                case LTE:
                    return (Date.class.isAssignableFrom(clazz)
                            || (clazz.isPrimitive() && !clazz.equals(boolean.class))
                            || Number.class.isAssignableFrom(clazz));
                case EX:
                    return String.class.equals(clazz);
                default:
                    return false;
            }
        }
    }

    /**
     *
     * @param tt
     * @param name
     * @param value
     * @return
     * @throws BadUsageException
     */
    protected Predicate buildPredicateWithOperator(Path<T> tt, String name, String value) throws BadUsageException {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        Operator operator = Operator.fromString(name);
        // perform operation, default operation is equal
        if (operator == null) {
            Path<T> attribute = tt.get(name);
            Object valueObject = convertStringValueToObject(value, attribute.getJavaType());
            System.out.println("### bp RETURN " + name + "=" + value);
            return criteriaBuilder.equal(attribute, valueObject);
        } else {
            Class javaType = tt.getJavaType();
            if (!classCompatibleWithOperator(javaType, operator)) {
                throw new BadUsageException(ExceptionType.BAD_USAGE_OPERATOR, operator.getValue() + " operator incompatible with field");
            }
            Object valueObject = convertStringValueToObject(value, javaType);
            switch (operator) {
                case GT:
                    return criteriaBuilder.greaterThan((Expression) tt, (Comparable) valueObject);
                case GTE:
                    return criteriaBuilder.greaterThanOrEqualTo((Expression) tt, (Comparable) valueObject);
                case LT:
                    return criteriaBuilder.lessThan((Expression) tt, (Comparable) valueObject);
                case LTE:
                    return criteriaBuilder.lessThanOrEqualTo((Expression) tt, (Comparable) valueObject);
                case NE:
                    return criteriaBuilder.notEqual(tt, valueObject);
                case EQ:
                    return criteriaBuilder.equal(tt, valueObject);
                case EX:
                    return criteriaBuilder.like((Expression) tt, value.replace('*', '%'));
                default: {
                    Path<T> attribute = tt.get(name);
                    valueObject = convertStringValueToObject(value, attribute.getJavaType());
                    return criteriaBuilder.equal(attribute, valueObject);
                }
            }
        }
    }

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