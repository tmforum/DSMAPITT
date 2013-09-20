/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.facade;

import java.util.List;
import javax.persistence.EntityManager;
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

    public void filterStuff() {
    }
}
