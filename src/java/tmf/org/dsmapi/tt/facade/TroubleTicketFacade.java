package tmf.org.dsmapi.tt.facade;

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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MultivaluedMap;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.ExceptionType;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
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

    public TroubleTicket edit(String id, TroubleTicket tt) throws UnknownResourceException {
        tt.setId(id);
        return super.edit(id, tt);
    }

    /**
     *
     * @param partialTT
     * @return
     */
    public TroubleTicket partialUpdate(TroubleTicket partialTT) throws BadUsageException, UnknownResourceException {

        TroubleTicket currentTT = this.find(partialTT.getId());

        if (currentTT == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }

        Set<TroubleTicketField> tokens = partialTT.getFields();

        if (tokens.contains(STATUS) & !(tokens.contains(STATUS_CHANGE_REASON))) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, "While updating 'status', please provide a 'statusChangeReason'");
        }

        if (tokens.contains(STATUS)) {
            if (WorkflowValidator.isCorrect(currentTT.getStatus(), partialTT.getStatus())) {
                currentTT.setStatus(partialTT.getStatus());
                currentTT.setStatusChangeDate(Format.toString(new Date()));
                currentTT.setStatusChangeReason(partialTT.getStatusChangeReason());
            } else {
                throw new BadUsageException(ExceptionType.BAD_USAGE_STATUS_TRANSITION, "current=" + currentTT.getStatus() + " sent=" + partialTT.getStatus());
            }
        }

        for (TroubleTicketField token : tokens) {
            switch (token) {
                case CORRELATION_ID:
                    currentTT.setCorrelationId(partialTT.getCorrelationId());
                    break;
                case CREATION_DATE:
                    currentTT.setCreationDate(partialTT.getCreationDate());
                    break;
                case DESCRIPTION:
                    if (partialTT.getDescription() != null) {
                        currentTT.setDescription(partialTT.getDescription());
                    }
                    break;
                case NOTES:
                    currentTT.setNotes(partialTT.getNotes());
                    break;
                case RELATED_OBJECTS:
                    currentTT.setRelatedObjects(partialTT.getRelatedObjects());
                    break;
                case RELATED_PARTIES:
                    currentTT.setRelatedParties(partialTT.getRelatedParties());
                    break;
                case RESOLUTION_DATE:
                    currentTT.setResolutionDate(partialTT.getResolutionDate());
                    break;
                case SEVERITY:
                    if (partialTT.getSeverity() != null) {
                        currentTT.setSeverity(partialTT.getSeverity());
                    }
                    break;
                case SUB_STATUS:
                    currentTT.setSubStatus(partialTT.getSubStatus());
                    break;
                case TARGET_RESOLUTION_DATE:
                    currentTT.setResolutionDate(partialTT.getResolutionDate());
                    break;
                case TYPE:
                    if (partialTT.getDescription() != null) {
                        currentTT.setType(partialTT.getType());
                    }
                    break;
            }
        }

        return currentTT;

    }

    @Override
    public void create(TroubleTicket tt) throws BadUsageException {

        if (tt.getId() != null) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_GENERIC, "While creating a TT id should be null");
        }

        if ((tt.getDescription() == null) || (tt.getSeverity() == null) || (tt.getType() == null)) {
            String fieldName = null;
            if (tt.getDescription() == null) {
                fieldName = TroubleTicketField.DESCRIPTION.getText();
            } else {
                if (tt.getSeverity() == null) {
                    fieldName = TroubleTicketField.SEVERITY.getText();
                } else if (tt.getType() == null) {
                    fieldName = TroubleTicketField.TYPE.getText();
                }
            }
            throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, fieldName);
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
            String key = sv.getKey();
            System.out.println(key);
            System.out.println(sv.getValue());
            TroubleTicketField fieldName = TroubleTicketField.fromString(key);
            if ((!key.equals("timestamp")) && (ReservedKeyword.fromString(key) == null) && fieldName != null) //timestamp : bug with netbeans test tool
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

    public int removeAll() {
        Query query = em.createQuery("DELETE FROM TroubleTicket tt");
        return query.executeUpdate();
    }

    @Override
    protected TroubleTicket getView(TroubleTicket fullElement, Set<String> fieldNames) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
