package tmf.org.dsmapi.tt;

import tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum;
import java.util.Calendar;
import java.util.Date;
import tmf.org.dsmapi.tt.model.TroubleTicket;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.MandatoryFieldException;
import tmf.org.dsmapi.commons.exceptions.StatusException;
import tmf.org.dsmapi.commons.utils.Format;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.CORRELATION_ID;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.CREATION_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.DESCRIPTION;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.NOTES;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.RELATED_OBJECTS;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.RELATED_PARTIES;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.RESOLUTION_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.SEVERITY;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.STATUS;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.STATUS_CHANGE_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.STATUS_CHANGE_REASON;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.SUB_STATUS;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.TARGET_RESOLUTION_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum.TYPE;
import tmf.org.dsmapi.tt.model.Status;

/**
 *
 * @author maig7313
 */
@Stateless
public class TroubleTicketFacade extends AbstractFacade<TroubleTicket> {

    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;

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
    public TroubleTicket partialUpdate(TroubleTicket partialTT) throws StatusException, BadUsageException {

        TroubleTicket targetTT = this.find(partialTT.getId());

        if (targetTT != null) {

            Set<TroubleTicketAttributesEnum> tokens = partialTT.getTokens();

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


            for (TroubleTicketAttributesEnum token : tokens) {
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

    private static TroubleTicket getView(TroubleTicket fullTT, Set<TroubleTicketAttributesEnum> tokens) {

        TroubleTicket resultTT = null;

        if (fullTT != null) {

            if (tokens.contains(TroubleTicketAttributesEnum.ALL)) {
                resultTT = fullTT;

            } else {
                resultTT = new TroubleTicket();
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
}
