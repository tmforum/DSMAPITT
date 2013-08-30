package tmf.org.dsmapi.tt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

/**
 *
 * @author maig7313
 *
 */
@Stateless
public class TroubleTicketFacade extends AbstractFacade<TroubleTicket> {

    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;
    private CriteriaBuilder cb;

    @PostConstruct
    private void init() {
        cb = em.getCriteriaBuilder();
    }

    /**
     *
     */
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
    /**
     *
     * @param id
     * @param tokens
     * @return
     */
    public TroubleTicket find(Object id, Set<TroubleTicketAttributesEnum> tokens) {

        TroubleTicket fullTT = super.find(id);

        TroubleTicket responseTT = getView(fullTT, tokens);

        return responseTT;
    }

    /**
     *
     * @param partialTT
     * @return
     */
    public TroubleTicket partialUpdate(TroubleTicket partialTT) {

        TroubleTicket targetTT = this.find(partialTT.getId());

        if (targetTT != null) {

            for (TroubleTicketAttributesEnum token : partialTT.getTokens()) {
                switch (token) {
                    case CORRELATION_ID:
                        targetTT.setCorrelationId(partialTT.getCorrelationId());
                        break;
                    case CREATION_DATE:
                        targetTT.setCreationDate(partialTT.getCreationDate());
                        break;
                    case DESCRIPTION:
                        if (partialTT.getDescription() != null) {
                            targetTT.setDescription(partialTT.getDescription());
                        }
                        break;
                    case NOTES:
                        targetTT.setNotes(partialTT.getNotes());
                        break;
                    case RELATED_OBJECTS:
                        targetTT.setRelatedObjects(partialTT.getRelatedObjects());
                        break;
                    case RELATED_PARTIES:
                        targetTT.setRelatedParties(partialTT.getRelatedParties());
                        break;
                    case RESOLUTION_DATE:
                        targetTT.setResolutionDate(partialTT.getResolutionDate());
                        break;
                    case SEVERITY:
                        if (partialTT.getSeverity() != null) {
                            targetTT.setSeverity(partialTT.getSeverity());
                        }
                        break;
                    case STATUS:
                        targetTT.setStatus(partialTT.getStatus());
                        break;
                    case STATUS_CHANGE_DATE:
                        targetTT.setStatusChangeDate(partialTT.getStatusChangeDate());
                        break;
                    case STATUS_CHANGE_REASON:
                        targetTT.setStatusChangeReason(partialTT.getStatusChangeReason());
                        break;
                    case SUB_STATUS:
                        targetTT.setSubStatus(partialTT.getSubStatus());
                        break;
                    case TARGET_RESOLUTION_DATE:
                        targetTT.setResolutionDate(partialTT.getResolutionDate());
                        break;
                    case TYPE:
                        if (partialTT.getDescription() != null) {
                            targetTT.setType(partialTT.getType());
                        }
                        break;
                }
            }
        }

        return targetTT;

    }

    /**
     *
     * @return
     */
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private static TroubleTicket getView(TroubleTicket fullTT, Set<TroubleTicketAttributesEnum> tokens) {

        TroubleTicket resultTT = null;

        if (fullTT != null) {

            if (tokens.contains(TroubleTicketAttributesEnum.ALL)) {
                resultTT = fullTT;

            } else {
                resultTT = new TroubleTicket();

                //      <xs:element name="id" type="xs:string" minOccurs="0"/>
                resultTT.setId(fullTT.getId());

                for (TroubleTicketAttributesEnum token : tokens) {
                    switch (token) {
                        case CORRELATION_ID:
                            resultTT.setCorrelationId(fullTT.getCorrelationId());
                            break;
                        case CREATION_DATE:
                            resultTT.setCreationDate(fullTT.getCreationDate());
                            break;
                        case DESCRIPTION:
                            resultTT.setDescription(fullTT.getDescription());
                            break;
                        case NOTES:
                            resultTT.setNotes(fullTT.getNotes());
                            break;
                        case RELATED_OBJECTS:
                            resultTT.setRelatedObjects(fullTT.getRelatedObjects());
                            break;
                        case RELATED_PARTIES:
                            resultTT.setRelatedParties(fullTT.getRelatedParties());
                            break;
                        case RESOLUTION_DATE:
                            resultTT.setResolutionDate(fullTT.getResolutionDate());
                            break;
                        case SEVERITY:
                            resultTT.setSeverity(fullTT.getSeverity());
                            break;
                        case STATUS:
                            resultTT.setStatus(fullTT.getStatus());
                            break;
                        case STATUS_CHANGE_DATE:
                            resultTT.setStatusChangeDate(fullTT.getStatusChangeDate());
                            break;
                        case STATUS_CHANGE_REASON:
                            resultTT.setStatusChangeReason(fullTT.getStatusChangeReason());
                            break;
                        case SUB_STATUS:
                            resultTT.setSubStatus(fullTT.getSubStatus());
                            break;
                        case TARGET_RESOLUTION_DATE:
                            resultTT.setResolutionDate(fullTT.getResolutionDate());
                            break;
                        case TYPE:
                            resultTT.setType(fullTT.getType());
                            break;
                    }

                }
            }
        }
        return resultTT;
    }

    public List<TroubleTicket> findByAttributeFilter(MultivaluedMap<String, String> map) {


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

        while (it.hasNext()) {
            Map.Entry<String, List<String>> sv = it.next();
            System.out.println(sv.getKey());
            System.out.println(sv.getValue());
            if (!sv.getKey().equals("timestamp")) //bug with netbeans test tool
            {
                Predicate predicate = buildPredicate(tt, sv.getKey(), sv.getValue().get(0));
                andPredicates.add(predicate);
            }
        }


        cq.where(andPredicates.toArray(new Predicate[andPredicates.size()]));
        cq.select(tt);
        TypedQuery<TroubleTicket> q = em.createQuery(cq);
        tickets = q.getResultList();
        return tickets;

    }

    Predicate buildPredicate(Root<TroubleTicket> tt, String name, String value) {
        Predicate predicate = null;
        if (name.equals("severity")) {
            predicate = cb.equal(tt.get(name), Severity.valueOf(value));
        } else {
            predicate = cb.equal(tt.get(name), value);
        }
        return predicate;
    }
}
