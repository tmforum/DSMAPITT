/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import tmf.org.dsmapi.tt.TroubleTicket;

/**
 *
 * @author pierregauthier
 */
@XmlRootElement
public class HubEvent implements Serializable {

    private TroubleTicket event; //checl for object
    private TroubleTicketEventTypeEnum eventType;

    public TroubleTicket getEvent() {
        return event;
    }

    public void setEvent(TroubleTicket event) {
        this.event = event;
    }

    public TroubleTicketEventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(TroubleTicketEventTypeEnum eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "HubEvent{" + "event=" + event + ", eventType=" + eventType + '}';
    }
}
