/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author pierregauthier {"ticket": { "id": 1, "correlationId": "TT53482",
 * "description": "Customer complaint over last invoice.", "severity": "Urgent",
 * "type": "Bills, charges or payment", "creationDate": "2013-07-23 08:16:39",
 * "targetResolutionDate": "2013-07-30 10:20:01", "status": "In Progress",
 * "subStatus": "Held", "statusChangeReason": "Waiting for invoicing expert.",
 * "statusChangeDate": "2013-07-08 08:55:12", "resolutionDate": "",
 * "relatedParties": [ { "role": "Originator", "reference": "/customer/1234" },
 * { "role": "Owner", "reference": "/operator/1234" }, { "role": "Reviser",
 * "reference": "Roger Collins" } ], "relatedObjects": [ { "involvement":
 * "Disputed", "reference": "/invoice/1234" }, { "involvement": "Adjusted",
 * "reference": "/invoice/5678" } ], "notes": [ { "date": "2013-07-19 09:55:30",
 * "author": "Arthur Evans", "text": "Already called the expert" }, { "date":
 * "2013-07-21 08:55:12", "author": "Arthur Evans", "text": "Informed the
 * originator" } ] }}
 *
 */
@Entity
@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TroubleTicket implements Serializable {

    //Add other static strings as required....
    private static final long serialVersionUID = 1L;
    // Used for incremental update
    @Transient
    @JsonIgnore
    private Set<TroubleTicketField> fieldsIN;
    @Id
    @Column(name="TT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    private String correlationId;
    private String description; 
    private Severity severity;
    private String type;
    private String creationDate;
    private String targetResolutionDate;
    private Status status;
    private SubStatus subStatus;
    private String statusChangeReason;
    private String statusChangeDate;
    private String resolutionDate;
    
    @ElementCollection
    @CollectionTable(
            name = "RELATED_OBJECT",
            joinColumns =
            @JoinColumn(name = "OWNER_ID"))
    private List<RelatedObject> relatedObjects;
    
    @ElementCollection
    @CollectionTable(
            name = "NOTES",
            joinColumns =
            @JoinColumn(name = "OWNER_ID"))
    private List<Note> notes;
    
    @ElementCollection
    @CollectionTable(
            name = "RELATED_PARTY",
            joinColumns =
            @JoinColumn(name = "OWNER_ID"))
    private List<RelatedParty> relatedParties;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getTargetResolutionDate() {
        return targetResolutionDate;
    }

    public void setTargetResolutionDate(String targetResolutionDate) {
        this.targetResolutionDate = targetResolutionDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SubStatus getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(SubStatus subStatus) {
        this.subStatus = subStatus;
    }

    public String getStatusChangeReason() {
        return statusChangeReason;
    }

    public void setStatusChangeReason(String statusChangeReason) {
        this.statusChangeReason = statusChangeReason;
    }

    public String getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(String statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    public String getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(String resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public List<RelatedObject> getRelatedObjects() {
        if (relatedObjects.isEmpty()) return null;
        else return relatedObjects;
    }

    public void setRelatedObjects(List<RelatedObject> relatedObjects) {
        this.relatedObjects = relatedObjects;
    }

    public List<Note> getNotes() {
        if (notes.isEmpty()) return null;
        else return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<RelatedParty> getRelatedParties() {
        if (relatedParties.isEmpty()) return null;
        else return relatedParties;
    }

    public void setRelatedParties(List<RelatedParty> relatedParties) {
        this.relatedParties = relatedParties;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    //this must be reimplemented
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fieldsIN are not set
        if (!(object instanceof TroubleTicket)) {
            return false;
        }
        TroubleTicket other = (TroubleTicket) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tmf.org.dsmtt.TroubleTicket[ id=" + id + " ]";
    }

    /**
     * @return the fieldsIN
     */
    public Set<TroubleTicketField> getFieldsIN() {
        return fieldsIN;
    }

    /**
     * @param fieldsIN the fieldsIN to set
     */
    public void setFieldsIN(Set<TroubleTicketField> fields) {
        this.fieldsIN = fields;
    }
}
