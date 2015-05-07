package org.tmf.dsmapi.troubleTicket.service;

import java.util.Date;
import java.util.Set;
import org.tmf.dsmapi.commons.facade.AbstractFacade;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.ExceptionType;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.utils.BeanUtils;
import org.tmf.dsmapi.commons.utils.TMFDate;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;
import org.tmf.dsmapi.troubleTicket.hub.service.TroubleTicketEventPublisherLocal;
import org.tmf.dsmapi.troubleTicket.model.Status;

/**
 *
 * @author pierregauthier
 */
@Stateless
public class TroubleTicketFacade extends AbstractFacade<TroubleTicket> {

    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;
    @EJB
    TroubleTicketEventPublisherLocal publisher;

    public TroubleTicketFacade() {
        super(TroubleTicket.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void create(TroubleTicket entity) throws BadUsageException {
        if (entity.getId() != null) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_GENERIC, "While creating TroubleTicket, id must be null");
        }

        super.create(entity);
    }

    public void checkCreation(TroubleTicket newTroubleTicket) throws BadUsageException {
        //verify first status
        if (null == newTroubleTicket.getStatus()) {
            newTroubleTicket.setStatus(Status.Submitted);
        } else {
            if (!newTroubleTicket.getStatus().name().equalsIgnoreCase(Status.Submitted.name())) {
                throw new BadUsageException(ExceptionType.BAD_USAGE_FLOW_TRANSITION, "status " + newTroubleTicket.getStatus().value() + " is not the first state, attempt : " + Status.Submitted.value());
            }
        }

        if (null == newTroubleTicket.getDescription()) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, "description is mandatory");
        }

        if (null == newTroubleTicket.getSeverity()) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, "severity is mandatory");
        }

        if (null == newTroubleTicket.getType()) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, "type is mandatory");
        }

        if (null == newTroubleTicket.getCreationDate()) {
            newTroubleTicket.setCreationDate(TMFDate.toString(new Date()));
        }

        if (null == newTroubleTicket.getStatusChangeDate()) {
            newTroubleTicket.setStatusChangeDate(TMFDate.toString(new Date()));
        }

    }

    public TroubleTicket checkPatch(long id, TroubleTicket partialTT) throws UnknownResourceException, BadUsageException {
        TroubleTicket currentTT = this.find(id);
        if (null == currentTT) {
            throw new BadUsageException(ExceptionType.UNKNOWN_RESOURCE);
        }
        System.out.println("id  " + id);
        System.out.println("entity before partial edit " + currentTT);

        if (null != partialTT.getId()) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_OPERATOR, "id is not patchable");
        }

        if (null != partialTT.getCorrelationId()) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_OPERATOR, "correlationId is not patchable");
        }

        if (null != partialTT.getCreationDate()) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_OPERATOR, "creationDate is not patchable");
        }

        if (null != partialTT.getStatus()) {
            if (null == partialTT.getStatusChangeReason()) {
                throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, "statusChangeReason is mandatory if status modified ");
            }
            if (WorkflowValidator.isCorrect(currentTT.getStatus(), partialTT.getStatus())) {
                currentTT.setStatus(partialTT.getStatus());

                currentTT.setStatusChangeDate(TMFDate.toString(new Date()));
                currentTT.setStatusChangeReason(partialTT.getStatusChangeReason());
                publisher.stateChangeNotification(currentTT, new Date());
            } else {
                throw new BadUsageException(ExceptionType.BAD_USAGE_FLOW_TRANSITION, "current=" + currentTT.getStatus() + " sent=" + partialTT.getStatus());
            }
        }
        //System.out.println("Before editing current");
//        super.edit(currentTT);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(partialTT, JsonNode.class);
        partialTT.setId(id);
        if (BeanUtils.patch(currentTT, partialTT, node)) {
            publisher.updateNotification(currentTT, new Date());
        }

        
        return currentTT;
    }

}
