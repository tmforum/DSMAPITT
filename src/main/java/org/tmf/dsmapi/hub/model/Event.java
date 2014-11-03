/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.hub.model;

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
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.commons.utils.TMFDate;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;

/**
 *
 * @author pierregauthier
 */
@XmlRootElement
@Entity
@JsonPropertyOrder(value = {"event", "reason", "dateEvent", "eventType"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Table(name="EventTroubleTicket")
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String reason;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = TMFDate.class)
    private Date dateEvent;
    private TroubleTicket resource; //checl for object
    @Enumerated(value = EnumType.STRING)
    private EventTypeEnum eventType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(EventTypeEnum eventType) {
        this.eventType = eventType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDate() {
        return dateEvent;
    }

    public void setDate(Date date) {
        this.dateEvent = date;
    }

    public TroubleTicket getResource() {
        return resource;
    }

    public void setResource(TroubleTicket resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "Event{" + "id=" + id + ", eventType=" + eventType + ", reason=" + reason + ", dateEvent=" + dateEvent + ", resource=" + resource + "}";
    }
}
