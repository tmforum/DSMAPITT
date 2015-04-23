package org.tmf.dsmapi.troubleTicket.hub.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.tmf.dsmapi.commons.facade.AbstractFacade;
import org.tmf.dsmapi.troubleTicket.hub.model.TroubleTicketEvent;

@Stateless
public class TroubleTicketEventFacade extends AbstractFacade<TroubleTicketEvent>{
    
    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;
   

    
    /**
     *
     */
    public TroubleTicketEventFacade() {
        super(TroubleTicketEvent.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
