package tmf.org.dsmapi.tt;

import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TroubleTicketFacade extends AbstractFacade<TroubleTicket> {

    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;

    public TroubleTicketFacade() {
        super(TroubleTicket.class);
    }

    /*
     * Find troubleTicket by id
     * Return only existing troubleTicket attributes specified in the tokenList
     * 
     * Token "all" return all attributes
     * 
     */
    public TroubleTicket find(Object id, List<String> tokenList) {

        TroubleTicket fullTT = super.find(id);

        TroubleTicket responseTT = filter(fullTT, tokenList);

        return responseTT;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private static TroubleTicket filter(TroubleTicket fullTT, List<String> tokenList) {

        TroubleTicket resultTT = null;

        if (fullTT != null) {

            if (tokenList.contains(TroubleTicket.ALL)) {
                resultTT = fullTT;
            } else {
                resultTT = new TroubleTicket();

                //      <xs:element name="id" type="xs:string" minOccurs="0"/>
                resultTT.setId(fullTT.getId());

                //      <xs:element name="correlationId" type="xs:string" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.CORRELATION_ID)) {
                    resultTT.setCorrelationId(fullTT.getCorrelationId());
                }
                //      <xs:element name="creationDate" type="xs:string" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.CREATION_DATE)) {
                    resultTT.setCreationDate(fullTT.getCreationDate());
                }
                //      <xs:element name="description" type="xs:string" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.DESCRIPTION)) {
                    resultTT.setDescription(fullTT.getDescription());
                }

                //      <xs:element name="notes" type="note" nillable="true" minOccurs="0" maxOccurs="unbounded"/>
                if (tokenList.contains(TroubleTicket.NOTES)) {
                    resultTT.setNotes(fullTT.getNotes());
                }
                //      <xs:element name="relatedObjects" type="relatedObject" nillable="true" minOccurs="0" maxOccurs="unbounded"/>
                if (tokenList.contains(TroubleTicket.RELATED_OBJECTS)) {
                    resultTT.setRelatedObjects(fullTT.getRelatedObjects());
                }

                //      <xs:element name="relatedParties" type="relatedParty" nillable="true" minOccurs="0" maxOccurs="unbounded"/>
                if (tokenList.contains(TroubleTicket.RELATED_PARTIES)) {
                    resultTT.setRelatedParties(fullTT.getRelatedParties());
                }

                //      <xs:element name="resolutionDate" type="xs:string" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.RESOLUTION_DATE)) {
                    resultTT.setResolutionDate(fullTT.getResolutionDate());
                }

                //      <xs:element name="severity" type="severity" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.SEVERITY)) {
                    resultTT.setSeverity(fullTT.getSeverity());
                }

                //      <xs:element name="status" type="status" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.STATUS)) {
                    resultTT.setStatus(fullTT.getStatus());
                }

                //      <xs:element name="statusChangeDate" type="xs:string" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.STATUS_CHANGE_DATE)) {
                    resultTT.setStatusChangeDate(fullTT.getStatusChangeDate());
                }

                //      <xs:element name="statusChangeReason" type="xs:string" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.STATUS_CHANGE_REASON)) {
                    resultTT.setStatusChangeReason(fullTT.getStatusChangeReason());
                }

                //      <xs:element name="subStatus" type="subStatus" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.SUB_STATUS)) {
                    resultTT.setSubStatus(fullTT.getSubStatus());
                }

                //      <xs:element name="targetResolutionDate" type="xs:string" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.TARGET_RESOLUTION_DATE)) {
                    resultTT.setResolutionDate(fullTT.getResolutionDate());
                }

                //      <xs:element name="type" type="xs:string" minOccurs="0"/>
                if (tokenList.contains(TroubleTicket.TYPE)) {
                    resultTT.setType(fullTT.getType());
                }

            }
        }
        return resultTT;
    }
}
