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
import javax.persistence.criteria.CriteriaBuilder;
import javax.ws.rs.core.MultivaluedMap;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.ExceptionType;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.commons.utils.Format;
import tmf.org.dsmapi.tt.Status;
import static tmf.org.dsmapi.tt.TroubleTicketField.CREATION_DATE;
import static tmf.org.dsmapi.tt.TroubleTicketField.DESCRIPTION;
import static tmf.org.dsmapi.tt.TroubleTicketField.NOTES;
import static tmf.org.dsmapi.tt.TroubleTicketField.RELATED_OBJECTS;
import static tmf.org.dsmapi.tt.TroubleTicketField.RELATED_PARTIES;
import static tmf.org.dsmapi.tt.TroubleTicketField.RESOLUTION_DATE;
import static tmf.org.dsmapi.tt.TroubleTicketField.SEVERITY;
import static tmf.org.dsmapi.tt.TroubleTicketField.STATUS;
import static tmf.org.dsmapi.tt.TroubleTicketField.STATUS_CHANGE_REASON;
import static tmf.org.dsmapi.tt.TroubleTicketField.TARGET_RESOLUTION_DATE;
import static tmf.org.dsmapi.tt.TroubleTicketField.TYPE;
import tmf.org.dsmapi.tt.workflow.StateModel;
import tmf.org.dsmapi.tt.workflow.StateModelTT;

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
    private StateModel stateModel;

    @PostConstruct
    private void init() {
        cb = em.getCriteriaBuilder();
        stateModel = new StateModelTT();
        int a = 3;
    }

    /**
     *
     */
    public TroubleTicketFacade() {
        super(TroubleTicket.class);
    }

    @Override
    public TroubleTicket edit(TroubleTicket tt) throws UnknownResourceException {
        TroubleTicket targetEntity = this.find(tt.getId());
        if (targetEntity == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }
        tt = super.edit(tt);
        return tt;
    }

    /**
     *
     * @param partialTT
     * @return
     */
    public TroubleTicket updateAttributes(TroubleTicket partialTT, Set<TroubleTicketField> fields) throws BadUsageException, UnknownResourceException {

        TroubleTicket currentTT = this.find(partialTT.getId());

        if (currentTT == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }

        if (fields.contains(STATUS) & !(fields.contains(STATUS_CHANGE_REASON))) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, "While updating 'status', please provide a 'statusChangeReason'");
        }
        
        // Allow status update when there is no correlationId, for demo or admin purpose
        if (fields.contains(STATUS) && (partialTT.getCorrelationId()==null)) {
            // isValidTransition if this transition is allowed
            stateModel.checkTransition(currentTT.getStatus(), partialTT.getStatus());
            currentTT.setStatus(partialTT.getStatus());
            currentTT.setStatusChangeDate(Format.toString(new Date()));
            currentTT.setStatusChangeReason(partialTT.getStatusChangeReason());
        }        

        for (TroubleTicketField token : fields) {
            switch (token) {
                case CREATION_DATE:
                    currentTT.setCreationDate(partialTT.getCreationDate());
                    break;
                case DESCRIPTION:
                    if (partialTT.getDescription() != null) {
                        currentTT.setDescription(partialTT.getDescription());
                    }
                    break;
                case NOTES:
                    currentTT.setNotes(partialTT.getNotes());  // Replace Notes
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
        em.merge(currentTT);
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

        super.create(tt);

    }

    public TroubleTicket updateStatus(TroubleTicket troubleTicket, Status status, String reason) {
        troubleTicket.setStatus(status);
        troubleTicket.setStatusChangeDate(Format.toString(new Date()));
        troubleTicket.setStatusChangeReason(reason);
        em.merge(troubleTicket);
        return troubleTicket;
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
