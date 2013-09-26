package tmf.org.dsmapi.tt.service;

import tmf.org.dsmapi.tt.TroubleTicketField;
import java.util.Date;
import java.util.List;
import tmf.org.dsmapi.tt.TroubleTicket;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.ws.rs.core.MultivaluedMap;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.ExceptionType;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.commons.utils.Format;
import static tmf.org.dsmapi.tt.TroubleTicketField.CORRELATION_ID;
import static tmf.org.dsmapi.tt.TroubleTicketField.CREATION_DATE;
import static tmf.org.dsmapi.tt.TroubleTicketField.DESCRIPTION;
import static tmf.org.dsmapi.tt.TroubleTicketField.NOTES;
import static tmf.org.dsmapi.tt.TroubleTicketField.RELATED_OBJECTS;
import static tmf.org.dsmapi.tt.TroubleTicketField.RELATED_PARTIES;
import static tmf.org.dsmapi.tt.TroubleTicketField.RESOLUTION_DATE;
import static tmf.org.dsmapi.tt.TroubleTicketField.SEVERITY;
import static tmf.org.dsmapi.tt.TroubleTicketField.STATUS;
import static tmf.org.dsmapi.tt.TroubleTicketField.STATUS_CHANGE_REASON;
import static tmf.org.dsmapi.tt.TroubleTicketField.SUB_STATUS;
import static tmf.org.dsmapi.tt.TroubleTicketField.TARGET_RESOLUTION_DATE;
import static tmf.org.dsmapi.tt.TroubleTicketField.TYPE;
import tmf.org.dsmapi.tt.Status;

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
    public TroubleTicket partialEdit(String id, TroubleTicket partialTT) throws BadUsageException, UnknownResourceException {

        TroubleTicket currentTT = this.find(id);

        if (currentTT == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }

        Set<TroubleTicketField> tokens = partialTT.getFieldsIN();

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

    public List<TroubleTicket> find(MultivaluedMap<String, String> queryParameters) {

        List<TroubleTicket> tickets;
        if (queryParameters != null && !queryParameters.isEmpty()) {
            tickets = findByCriteria(queryParameters, TroubleTicket.class);
        } else {
            tickets = this.findAll();
        }
        return tickets;

    }

    public int removeAll() {
        List<TroubleTicket> tickets = this.findAll();
        int size = tickets.size();
        for (TroubleTicket tt : tickets) {
            em.remove(tt);
        }
        return size;
    }
}
