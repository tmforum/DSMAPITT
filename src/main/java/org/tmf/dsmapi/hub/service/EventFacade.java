package org.tmf.dsmapi.hub.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.tmf.dsmapi.commons.facade.AbstractFacade;
import org.tmf.dsmapi.hub.model.Event;

/**
 *
 * @author pierregauthier
 */
@Stateless
public class EventFacade extends AbstractFacade<Event> {
    
    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;
   

    
    /**
     *
     */
    public EventFacade() {
        super(Event.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
