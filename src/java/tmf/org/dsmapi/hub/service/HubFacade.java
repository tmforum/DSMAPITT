/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    
    public int removeAll() {
        List<Hub> entities = this.findAll();
        int size = entities.size();
        for (Hub entity : entities) {
            em.remove(entity);
        }
        return size;
    }    

}
