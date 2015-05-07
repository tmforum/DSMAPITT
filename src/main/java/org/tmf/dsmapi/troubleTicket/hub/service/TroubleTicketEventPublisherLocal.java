package org.tmf.dsmapi.troubleTicket.hub.service;

import java.util.Date;
import javax.ejb.Local;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;
import org.tmf.dsmapi.troubleTicket.hub.model.TroubleTicketEvent;


/**
 *
 * @author pierregauthier
 */
@Local
public interface TroubleTicketEventPublisherLocal {

    void publish(TroubleTicketEvent event);

    /**
     *
     * clearanceRequestNotification
     * @param bean the bean which has been created
     * @param date the creation date
     */
    public void clearanceRequestNotification(TroubleTicket bean, Date date);

    /**
     *
     * informationRequiredNotification
     * @param bean the bean which has been deleted
     * @param date the deletion date
     */
    public void informationRequiredNotification(TroubleTicket bean, Date date);

    /**
     *
     * UpdateNotification (PATCH)
     * @param bean the bean which has been updated
     * @param date the update date
     */
    public void updateNotification(TroubleTicket bean, Date date);

    /**
     *
     * stateChangeNotification
     * @param bean the bean which has been changed
     * @param date the change date
     */
    public void stateChangeNotification(TroubleTicket bean, Date date);
}
