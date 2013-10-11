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
    private StateModel stateModel;

    @PostConstruct
    private void init() {
        stateModel = new StateModelTT();
    }

    /**
     *
     */
    public TroubleTicketFacade() {
        super(TroubleTicket.class);
    }

    /**
     *
     * @param tt
     * @return
     * @throws UnknownResourceException
     */
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
     * @param patchTT
     * @param fields 
     * @return
     * @throws BadUsageException
     * @throws UnknownResourceException  
     */
    public TroubleTicket updateAttributes(TroubleTicket patchTT, Set<TroubleTicketField> fields) throws BadUsageException, UnknownResourceException {

        TroubleTicket currentTT = this.find(patchTT.getId());

        if (currentTT == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }

        if (fields.contains(STATUS) & !(fields.contains(STATUS_CHANGE_REASON))) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, "While updating 'status', please provide a 'statusChangeReason'");
        }
        
        // Allow status update when there is no correlationId, for demo or admin purpose
        if (fields.contains(STATUS) && (patchTT.getCorrelationId()==null)) {
            // isValidTransition if this transition is allowed
            stateModel.checkTransition(currentTT.getStatus(), patchTT.getStatus());
            currentTT.setStatus(patchTT.getStatus());
            currentTT.setStatusChangeDate(Format.toString(new Date()));
            currentTT.setStatusChangeReason(patchTT.getStatusChangeReason());
        }                

        for (TroubleTicketField token : fields) {
            switch (token) {
                case CREATION_DATE:
                    currentTT.setCreationDate(patchTT.getCreationDate());
                    break;
                case DESCRIPTION:
                    if (patchTT.getDescription() != null) {
                        currentTT.setDescription(patchTT.getDescription());
                    }
                    break;
                case NOTES:
                    currentTT.setNotes(patchTT.getNotes());  // Replace Notes
                    break;
                case RELATED_OBJECTS:
                    currentTT.setRelatedObjects(patchTT.getRelatedObjects());
                    break;
                case RELATED_PARTIES:
                    currentTT.setRelatedParties(patchTT.getRelatedParties());
                    break;
                case RESOLUTION_DATE:
                    currentTT.setResolutionDate(patchTT.getResolutionDate());
                    break;
                case SEVERITY:
                    if (patchTT.getSeverity() != null) {
                        currentTT.setSeverity(patchTT.getSeverity());
                    }
                    break;
                case TARGET_RESOLUTION_DATE:
                    currentTT.setResolutionDate(patchTT.getResolutionDate());
                    break;
                case TYPE:
                    if (patchTT.getType() != null) {
                        currentTT.setType(patchTT.getType());
                    }
                    break;
            }
        }
        em.merge(currentTT);
        return currentTT;
    }
    
    /**
     *
     * @param troubleTicket
     * @param status
     * @param reason
     * @return
     */
    public TroubleTicket updateStatus(TroubleTicket troubleTicket, Status status, String reason) {
        troubleTicket.setStatus(status);
        troubleTicket.setStatusChangeDate(Format.toString(new Date()));
        troubleTicket.setStatusChangeReason(reason);
        em.merge(troubleTicket);
        return troubleTicket;
    }    

    /**
     *
     * @param tt
     * @throws BadUsageException
     */
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

    /**
     *
     * @return
     */
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     *
     * @param queryParameters
     * @return
     */
    public List<TroubleTicket> find(MultivaluedMap<String, String> queryParameters) {

        List<TroubleTicket> tickets;
        if (queryParameters != null && !queryParameters.isEmpty()) {
            tickets = findByCriteria(queryParameters, TroubleTicket.class);
        } else {
            tickets = this.findAll();
        }
        return tickets;

    }

    /**
     *
     * @return
     */
    public int removeAll() {
        List<TroubleTicket> tickets = this.findAll();
        int size = tickets.size();
        for (TroubleTicket tt : tickets) {
            em.remove(tt);
        }
        return size;
    }
}
