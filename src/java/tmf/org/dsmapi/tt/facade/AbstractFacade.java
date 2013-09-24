/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.facade;

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

    public void clear() {
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

    public List<T> findAllWithFields(Set<String> fieldNames) {
        List<T> list = findAll();
        return getViewList(list, fieldNames);
    }

    public List<T> findByCriteriaWithFields(MultivaluedMap<String, String> map, Set<String> fieldNames, Class<T> clazz) {
        List<T> list = findByCriteria(map, clazz);
        return getViewList(list, fieldNames);
}

    private List<T> getViewList(List<T> list, Set<String> fieldNames) {
        List<T> resultList = new ArrayList<T>(list.size());
        for (T fullElement : list) {
            T viewElement = getView(fullElement, fieldNames);
            resultList.add(viewElement);
        }
        return resultList;
    }

    protected abstract T getView(T fullElement, Set<String> fieldNames);

    public List<T> findByCriteria(MultivaluedMap<String, String> map, Class<T> clazz) {
        List<T> resultsList = null;
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

    private Predicate buildSimplePredicate(Path<T> tt, String name, String value) {
        Predicate predicate;
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        if (isMultipleOrValue(value)) {
            // name=value1,value2,...,valueN
            // => name=value1 OR name=value2 OR ... OR name=valueN
            List<String> valueList = convertMultipleOrValueToList(value);
            List<Predicate> orPredicates = new ArrayList<Predicate>();
            for (String currentValue : valueList) {
                Predicate orPredicate = buildPredicateWithOperator(tt, name, currentValue);
                orPredicates.add(orPredicate);
            }
            predicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
        } else if (isMultipleAndValue(value)) {
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

    private Predicate buildPredicateWithOperator(Path<T> tt, String name, String value) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        Operator operator = Operator.fromString(name);
        // perform operation, default operation is equal
        if (operator == null) {
            return criteriaBuilder.equal(tt.get(name), value);
        } else {
            switch (operator) {
                case GT:
                    return criteriaBuilder.greaterThan((Expression) tt, value);
                case GTE:
                    return criteriaBuilder.greaterThanOrEqualTo((Expression) tt, value);
                case LT:
                    return criteriaBuilder.lessThan((Expression) tt, value);
                case LTE:
                    return criteriaBuilder.lessThanOrEqualTo((Expression) tt, value);
                case NE:
                    return criteriaBuilder.notEqual(tt, value);
                case EQ:
                    return criteriaBuilder.equal(tt, value);
                case EX:
                    return criteriaBuilder.like((Expression) tt, value.replace('*', '%'));
                default:
                    return criteriaBuilder.equal(tt.get(name), value);
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
