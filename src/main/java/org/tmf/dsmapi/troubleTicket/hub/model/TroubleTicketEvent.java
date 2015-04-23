/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.troubleTicket.hub.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.commons.utils.CustomJsonDateSerializer;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;

/**
 *
 * @author pierregauthier
 */
@XmlRootElement
@Entity
@Table(name="Event_TroubleTicket")
@JsonPropertyOrder(value = {"event",  "date", "eventType"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TroubleTicketEvent implements Serializable {
    
    public TroubleTicketEvent() {
        System.out.println("TroubleTicketEvent constructor");
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("eventId")
    private String id;
   
    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Date eventTime;
    @JsonIgnore
    private TroubleTicket resource; //check for object
    @Enumerated(value = EnumType.STRING)
    private TroubleTicketEventTypeEnum eventType;
    
    @JsonAutoDetect(fieldVisibility = ANY)
    class EventBody {
        private TroubleTicket troubleTicket;
        public TroubleTicket getTroubleTicket() {
            return troubleTicket;
        }
        public EventBody(TroubleTicket troubleTicket) { 
        this.troubleTicket = troubleTicket;
    }
    
       
    }
   @JsonProperty("event")
   public EventBody getEvent() {
       
       return new EventBody(getResource() );
   }
    
  @JsonIgnore 
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TroubleTicketEventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(TroubleTicketEventTypeEnum eventType) {
        this.eventType = eventType;
    }
/*
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
*/
    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    
@JsonIgnore
    public TroubleTicket getResource() {
        
        
        return resource;
    }

    public void setResource(TroubleTicket resource) {
        this.resource = resource;
    }

    
}
