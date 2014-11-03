package org.tmf.dsmapi.hub.service;

import java.util.Date;
import javax.ejb.Local;
import org.tmf.dsmapi.hub.model.Event;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;



/**
 *
 * @author pierregauthier
 */
@Local
public interface EventPublisherLocal {

    void publish(Event event);

    /**
     *
     * @param tt
     */
    public void createNotification(TroubleTicket bean, String reason, Date date);

    /**
     *
     * @param tt
     */
    public void statusChangedNotification(TroubleTicket bean, String reason, Date date);

    /**
     *
     * @param tt
     */
    public void changedNotification(TroubleTicket bean, String reason, Date date);

    /**
     *
     * @param tt
     */
    public void clearanceNotification(TroubleTicket bean, String reason, Date date);
   
    /**
     *
     * @param tt
     */
    public void informationRequiredNotification(TroubleTicket bean, String reason, Date date);
   
}
