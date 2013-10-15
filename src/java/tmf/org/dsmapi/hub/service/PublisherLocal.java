/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import java.util.Date;
import javax.ejb.Local;
import tmf.org.dsmapi.tt.TroubleTicket;


/**
 *
 * @author pierregauthier
 */
@Local
public interface PublisherLocal {

    void publish(Object event);

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
    public void clearanceRequestNotification(TroubleTicket bean, String reason, Date date);

    /**
     *
     * @param tt
     */
    public void informationRequiredNotification(TroubleTicket bean, String reason, Date date);
   
    
}
