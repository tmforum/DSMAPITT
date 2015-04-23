package org.tmf.dsmapi.subscriber.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.tmf.dsmapi.commons.facade.AbstractFacade;
import org.tmf.dsmapi.subscriber.model.EventBag;

/**
 *
 * @author ecus6396
 */
@Stateless
public class EventApiFacade extends AbstractFacade<EventBag> {

    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;

    public EventApiFacade() {
        super(EventBag.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
