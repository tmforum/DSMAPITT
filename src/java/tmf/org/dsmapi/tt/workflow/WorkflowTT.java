package tmf.org.dsmapi.tt.workflow;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import tmf.org.dsmapi.commons.utils.Format;
import tmf.org.dsmapi.hub.service.PublisherLocal;
import tmf.org.dsmapi.tt.Note;
import tmf.org.dsmapi.tt.Status;
import tmf.org.dsmapi.tt.TroubleTicket;
import tmf.org.dsmapi.tt.TroubleTicketField;
import tmf.org.dsmapi.tt.service.TroubleTicketFacade;

/**
 *
 * @author maig7313
 */
@Stateless
@Asynchronous
public class WorkflowTT implements WorkFlow<TroubleTicket> {

    private final static long PAUSE = 3000;
    @EJB
    TroubleTicketFacade manager;
    @EJB
    PublisherLocal publisher;

    /**
     *
     * @param tt
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void start(TroubleTicket tt) {
        try {
            publisher.createNotification(tt);
            Thread.sleep(PAUSE);

            this.afterCreateExecute(tt);
            publisher.statusChangedNotification(tt); // > Acknowledeged
            Thread.sleep(PAUSE);

            this.afterAcknowledged(tt);
            publisher.statusChangedNotification(tt); // > In Progress        
            Thread.sleep(PAUSE);

            this.afterInProgress(tt);
            publisher.statusChangedNotification(tt); // > In Progress Held OR In Progress Pending Or Resolved
            Thread.sleep(PAUSE);

            this.route(tt);

        } catch (InterruptedException ex) {
            Logger.getLogger(WorkflowTT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param tt
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void wakeUp(TroubleTicket tt) {

        try {

            if (Status.Resolved == tt.getStatus()) {
                this.afterResolved(tt);
                publisher.statusChangedNotification(tt); // > Closed  
            }

            if (Status.InProgress_Held == tt.getStatus()) {
                this.afterInProgressHeld(tt);
                publisher.statusChangedNotification(tt);  // > In Progress
                Thread.sleep(PAUSE);
                this.afterInProgress(tt);
                publisher.statusChangedNotification(tt); // > In Progress Held OR In Progress Pending Or Resolved
                Thread.sleep(PAUSE);
                this.route(tt);
            }

            if (Status.InProgress_Pending == tt.getStatus()) { // > In Progress Pending
                this.afterInProgressPending(tt);
                publisher.statusChangedNotification(tt);  // > In Progress
                Thread.sleep(PAUSE);
                this.afterInProgress(tt);
                publisher.statusChangedNotification(tt); // > In Progress Held OR In Progress Pending Or Resolved
                Thread.sleep(PAUSE);
                this.route(tt);
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(WorkflowTT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param tt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void afterCreateExecute(TroubleTicket tt) {
        manager.updateStatus(tt, Status.Acknowledged, "The ticket is valid, will be proceed soon");
    }

    /**
     *
     * @param tt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void afterAcknowledged(TroubleTicket tt) {
        manager.updateStatus(tt, Status.InProgress, "Analysing Ticket");
    }

    /**
     *
     * @param tt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void afterInProgress(TroubleTicket tt) {

        List<Note> notes = tt.getNotes();

        if (notes.isEmpty()) {
            manager.updateStatus(tt, Status.InProgress_Pending, "Access Seeker action/information required");
        }
        String lastText = notes.get(notes.size() - 1).getText();

        if ("To Held".equalsIgnoreCase(lastText)) {
            manager.updateStatus(tt, Status.InProgress_Held, "Internal Operator action/information required");
        } else if ("To Resolved".equalsIgnoreCase(lastText)) {
            manager.updateStatus(tt, Status.Resolved, "Resolved");
        } else {
            manager.updateStatus(tt, Status.InProgress_Pending, "Access Seeker action/information required");
        }
    }

    /**
     *
     * @param tt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void afterInProgressHeld(TroubleTicket tt) {
        manager.updateStatus(tt, Status.InProgress, "Analysing Ticket");
    }

    /**
     *
     * @param tt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void afterInProgressPending(TroubleTicket tt) {
        manager.updateStatus(tt, Status.InProgress, "Analysing Ticket");
    }

    /**
     *
     * @param tt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void afterResolved(TroubleTicket tt) {
        manager.updateStatus(tt, Status.Closed, "Closed");
    }

    /**
     *
     * @param tt
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void route(TroubleTicket tt) {
        if (Status.InProgress_Held == tt.getStatus()) { // > In Progress Held
            TroubleTicket partialTT = new TroubleTicket();
            partialTT.setId(tt.getId()); // Set ID

            Note note = new Note();
            note.setAuthor("Mock workflow");
            note.setDate(Format.toString(new Date()));
            note.setText("To Resolved");
            partialTT.getNotes().add(note); // Set Note

            Set<TroubleTicketField> fields = new HashSet<TroubleTicketField>();
            fields.add(TroubleTicketField.NOTES);
            try {
                tt = manager.updateAttributes(partialTT, fields);
            } catch (Exception ex) {
                Logger.getLogger(WorkflowTT.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.wakeUp(tt);
        }

        if (Status.Resolved == tt.getStatus()) { // > Resolved
            this.wakeUp(tt); // Internal wakeUp
        }

        if (Status.InProgress_Pending == tt.getStatus()) { // > In Progress Pending
            // will be wakeUp by Access Seeker with a REST PATCH correlated TT
        }
    }
}
