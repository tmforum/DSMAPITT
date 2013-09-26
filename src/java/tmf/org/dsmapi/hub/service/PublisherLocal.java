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

    public void publishTicketCreateNotification(TroubleTicket tt);

    public void publishTicketStatusChangedNotification(TroubleTicket tt);

    public void publishTicketChangedNotification(TroubleTicket tt);

    public void publishTicketClearanceRequestNotification(TroubleTicket tt);

    public void publishInformationRequiredNotification(TroubleTicket tt);
   
    
}
