/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import tmf.org.dsmapi.hub.Hub;

/**
 *
 * @author pierregauthier
 */
@Stateless
public class RESTEventPublisher implements RESTEventPublisherLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
   @Asynchronous
   public void publish( Hub hub, Object event) {
        
       PostEventClient client = new PostEventClient(hub.getCallback());
       
       client.publishEvent(event);
        
       System.out.println("Sending Event");
    }

}
