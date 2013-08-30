package tmf.org.dsmapi.tt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MultivaluedMap;

@Stateless
public class TroubleTicketFacade extends AbstractFacade<TroubleTicket> {

    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;
    
   
    private CriteriaBuilder cb;

    @PostConstruct
    private void init() {
        cb = em.getCriteriaBuilder();
    }

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
    
   public  List<TroubleTicket> findByAttributeFilter(MultivaluedMap<String, String> map) {
       List<TroubleTicket> tickets = null;
       
    
        Iterator<Map.Entry<String, List<String>>> it = map.entrySet().iterator();
       
       
       CriteriaQuery<TroubleTicket> cq = cb.createQuery(TroubleTicket.class);
          List<Predicate> andPredicates = new ArrayList<Predicate>();
         Root<TroubleTicket> tt = cq.from(TroubleTicket.class);
         //adding multiple &
         //adding oring 
         //adding greater than 
         //adding regular expression
         //use Map as Entry
         //Predicate predicate = cb.equal(tt.get(name), Severity.valueOf(value));
         
        String attName = null;
        List<String> value = null;
       
         while(it.hasNext()) { 
            Map.Entry<String, List<String>> sv = it.next();
            System.out.println(sv.getKey());
            System.out.println(sv.getValue());
            if(!sv.getKey().equals("timestamp")) //bug with netbeans test tool
            {
            Predicate predicate = buildPredicate(tt, sv.getKey() ,sv.getValue().get(0));
            andPredicates.add(predicate);
            }
         }
         
         
         cq.where(andPredicates.toArray(new Predicate[andPredicates.size()]));
         cq.select(tt);
         TypedQuery<TroubleTicket> q = em.createQuery(cq);
         tickets = q.getResultList();
         return tickets;
         
    }
   
    Predicate buildPredicate( Root<TroubleTicket> tt, String name, String  value) {
        Predicate predicate = null;
        if ( name.equals("severity") )predicate = cb.equal(tt.get(name), Severity.valueOf(value));
        else predicate = cb.equal(tt.get(name), value);
        return predicate;
    }
}
