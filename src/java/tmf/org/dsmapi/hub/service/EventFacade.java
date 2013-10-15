/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import tmf.org.dsmapi.hub.HubEvent;
import tmf.org.dsmapi.tt.service.AbstractFacade;

/**
 *
 * @author pierregauthier
 */
@Stateless
public class EventFacade extends AbstractFacade<HubEvent>{
    
    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;
   

    
    /**
     *
     */
    public EventFacade() {
        super(HubEvent.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
