/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import javax.ejb.Local;
import tmf.org.dsmapi.tt.TroubleTicket;

/**
 *
 * @author pierregauthier
 */
@Local
public interface PublisherLocal {

    /**
     *
     * @param event
     */
    void publish(Object event);

    /**
     *
     * @param tt
     */
    public void createNotification(TroubleTicket tt);

    /**
     *
     * @param tt
     */
    public void statusChangedNotification(TroubleTicket tt);

    /**
     *
     * @param tt
     */
    public void changedNotification(TroubleTicket tt);

    /**
     *
     * @param tt
     */
    public void clearanceRequestNotification(TroubleTicket tt);

    /**
     *
     * @param tt
     */
    public void informationRequiredNotification(TroubleTicket tt);
   
    
}
