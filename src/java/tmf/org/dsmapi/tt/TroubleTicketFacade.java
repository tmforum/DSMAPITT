package tmf.org.dsmapi.tt;

import java.util.ArrayList;
import tmf.org.dsmapi.tt.model.TroubleTicketField;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tmf.org.dsmapi.tt.model.TroubleTicket;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MultivaluedMap;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.MandatoryFieldException;
import tmf.org.dsmapi.commons.exceptions.StatusException;
import tmf.org.dsmapi.commons.utils.Format;
import tmf.org.dsmapi.tt.model.Severity;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.CORRELATION_ID;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.CREATION_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.DESCRIPTION;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.NOTES;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.RELATED_OBJECTS;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.RELATED_PARTIES;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.RESOLUTION_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.SEVERITY;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.STATUS;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.STATUS_CHANGE_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.STATUS_CHANGE_REASON;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.SUB_STATUS;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.TARGET_RESOLUTION_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.TYPE;
import tmf.org.dsmapi.tt.model.Status;

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
     *
     * @param id
     * @param tokens
     * @return
     */
    public TroubleTicket find(Object id) {

        TroubleTicket fullTT = super.find(id);
        return fullTT;
        
    }

    /**
     *
     * @param partialTT
     * @return
     */
    public TroubleTicket partialUpdate(TroubleTicket partialTT) throws StatusException, BadUsageException {

        TroubleTicket targetTT = this.find(partialTT.getId());

        if (targetTT != null) {

            Set<TroubleTicketField> tokens = partialTT.getFields();

            if (tokens.contains(STATUS) & !(tokens.contains(STATUS_CHANGE_REASON))) {
                throw new BadUsageException();
            }

            if (tokens.contains(STATUS)) {
                if (Validator.isStatusUpdateValid(targetTT.getStatus(), partialTT.getStatus())) {
                    targetTT.setStatus(partialTT.getStatus());
                    targetTT.setStatusChangeDate(Format.toString(new Date()));
                    targetTT.setStatusChangeReason(partialTT.getStatusChangeReason());
                } else {
                    throw new StatusException();
                }
            }


            for (TroubleTicketField token : tokens) {
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

    @Override
    public void create(TroubleTicket tt) throws MandatoryFieldException {

        if (!Validator.hasMandatoryFields(tt)) {
            throw new MandatoryFieldException();
        }

        tt.setStatus(Status.Submitted);
        tt.setStatusChangeDate(Format.toString(new Date()));
        tt.setStatusChangeReason("Creation");
        super.create(tt);
    }

    /**
     *
     * @return
     */
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TroubleTicket> find(MultivaluedMap<String, String> map) {

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

        // Use .fromString not valueOf for Enum, to avoid case sensitive problem
        
        if (name.equalsIgnoreCase("status")) {
            return cb.equal(tt.get(name), Status.fromString(value));
        }

        if (name.equalsIgnoreCase("severity")) {
            return cb.equal(tt.get(name), Severity.fromString(value));
        }

        return cb.equal(tt.get(name), value);

    }
}
