package org.tmf.dsmapi.hub.service;

import javax.ejb.Local;
import org.tmf.dsmapi.hub.model.Event;
import org.tmf.dsmapi.hub.model.Hub;

/**
 *
 * @author pierregauthier
 */
@Local
public interface RESTEventPublisherLocal {

    public void publish(Hub hub, Event event);
    
}
