package org.tmf.dsmapi.troubleTicket.service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.MultivaluedMap;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.ExceptionType;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.facade.AbstractFacade;
import org.tmf.dsmapi.commons.utils.TMFDate;
import org.tmf.dsmapi.hub.service.EventPublisherLocal;
import org.tmf.dsmapi.troubleTicket.model.Status;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicketField;
import static org.tmf.dsmapi.troubleTicket.model.TroubleTicketField.STATUS;
import static org.tmf.dsmapi.troubleTicket.model.TroubleTicketField.STATUS_CHANGE_REASON;
        
/**
 *
 * @author maig7313
 *
 */
@Stateless
public class TroubleTicketFacade extends AbstractFacade<TroubleTicket> {

    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;
    private static long delay = 3000;

    @EJB
    EventPublisherLocal publisher;

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
        if (fields.contains(STATUS) && (patchTT.getCorrelationId() == null)) {
            // isValidTransition if this transition is allowed
            currentTT.setStatus(patchTT.getStatus());
            currentTT.setStatusChangeDate(TMFDate.toString(new Date()));
            currentTT.setStatusChangeReason(patchTT.getStatusChangeReason());
            // notify Status change
            publisher.changedNotification(currentTT, null, new Date());
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

        // Event tt changed
        publisher.changedNotification(currentTT, null, new Date());
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
        troubleTicket.setStatusChangeDate(TMFDate.toString(new Date()));
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
     * @return the delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * @param delay the delay to set
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }
}
