package org.tmf.dsmapi.hub.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.hub.model.Event;
import org.tmf.dsmapi.hub.model.EventTypeEnum;
import org.tmf.dsmapi.hub.model.Hub;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;

/**
 *
 * @author pierregauthier should be async or called with MDB
 */
@Stateless
@Asynchronous
public class EventPublisher implements EventPublisherLocal {

    @EJB
    HubFacade hubFacade;
    @EJB
    EventFacade eventFacade;
    @EJB
    RESTEventPublisherLocal restEventPublisherLocal;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    //Access Hubs using callbacks and send to http publisher 
    //(pool should be configured around the RESTEventPublisher bean)
    //Loop into array of Hubs
    //Call RestEventPublisher - Need to implement resend policy plus eviction
    //Filtering is done in RestEventPublisher based on query expression
    @Override
    public void publish(Event event) {
        try {
            eventFacade.create(event);
        } catch (BadUsageException ex) {
            Logger.getLogger(EventPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Hub> hubList = hubFacade.findAll();
        Iterator<Hub> it = hubList.iterator();
        while (it.hasNext()) {
            Hub hub = it.next();
            restEventPublisherLocal.publish(hub, event);
        }
    }

    @Override
    public void createNotification(TroubleTicket bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.TicketCreateNotification);
        publish(event);

    }

    @Override
    public void changedNotification(TroubleTicket bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.TicketChangedNotification);
        publish(event);
    }

    @Override
    public void statusChangedNotification(TroubleTicket bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.TicketStatusChangedNotification);
        publish(event);
    }

    @Override
    public void clearanceNotification(TroubleTicket bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.TicketClearanceRequestNotification);
        publish(event);
    }

    @Override
    public void informationRequiredNotification(TroubleTicket bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.InformationRequiredNotification);
        publish(event);
    }
}
