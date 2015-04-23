package org.tmf.dsmapi.troubleTicket.hub.service;

import javax.ejb.Local;
import org.tmf.dsmapi.troubleTicket.hub.model.TroubleTicketEvent;
import org.tmf.dsmapi.hub.model.Hub;

@Local
public interface TroubleTicketRESTEventPublisherLocal {

    public void publish(Hub hub, TroubleTicketEvent event);
    
}
