package org.tmf.dsmapi.troubleTicket.service;

import java.util.Date;
import java.util.Set;
import org.tmf.dsmapi.commons.facade.AbstractFacade;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.ExceptionType;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.utils.Format;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;
import org.tmf.dsmapi.troubleTicket.hub.service.TroubleTicketEventPublisherLocal;

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
    
    
    public void partialEdit(TroubleTicket partialTT) throws UnknownResourceException,BadUsageException {
        
        //System.out.println("In partialEdit");
        if (partialTT.getId() == null) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_GENERIC, "While creating TroubleTicket, id must not be null");
        }
        //System.out.println("Before find(partialTT.getId())");
        TroubleTicket currentTT = find(partialTT.getId());
        
         boolean STATUS = true;
         boolean STATUS_CHANGE_REASON =true;
         
         if ((STATUS) & !(STATUS_CHANGE_REASON)) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, "While updating 'status', please provide a 'statusChangeReason'");
        }
        //System.out.println("Before going in Validator");
        if (STATUS) {
            if (WorkflowValidator.isCorrect(currentTT.getStatus(), partialTT.getStatus())) {
                currentTT.setStatus(partialTT.getStatus());
                
                currentTT.setStatusChangeDate(Format.toString(new Date()));
                currentTT.setStatusChangeReason(partialTT.getStatusChangeReason());
            } else {
                throw new BadUsageException(ExceptionType.BAD_USAGE_STATUS_TRANSITION, "current=" + currentTT.getStatus() + " sent=" + partialTT.getStatus());
            }
        }

        //System.out.println("Before editing current");
        super.edit(currentTT);
    }

    
}
