/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import javax.ejb.Local;
import tmf.org.dsmapi.hub.Hub;

/**
 *
 * @author pierregauthier
 */
@Local
public interface RESTEventPublisherLocal {

    public void publish(Hub hub, Object event);
    
}
