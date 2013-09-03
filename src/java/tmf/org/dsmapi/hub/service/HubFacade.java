/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import tmf.org.dsmapi.hub.Hub;
import tmf.org.dsmapi.tt.TroubleTicket;

/**
 *
 * @author pierregauthier
 */
@Stateless
public class HubFacade extends AbstractFacade<Hub>{
    
    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;
   

    
    /**
     *
     */
    public HubFacade() {
        super(Hub.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
