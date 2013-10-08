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

   void publish(Object event);

    public void createNotification(TroubleTicket tt);

    public void statusChangedNotification(TroubleTicket tt);

    public void changedNotification(TroubleTicket tt);

    public void clearanceRequestNotification(TroubleTicket tt);

    public void informationRequiredNotification(TroubleTicket tt);
   
    
}
