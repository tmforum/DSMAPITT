package org.tmf.dsmapi.troubleTicket.service;


import org.tmf.dsmapi.commons.workflow.StateModelBase;
import org.tmf.dsmapi.troubleTicket.model.Status;

/**
 *
 * @author maig7313
 */
public class StateModelImpl extends StateModelBase<Status> {
    
    /**
     *
     */
    public StateModelImpl() {
        super(Status.class);
    }    

    /**
     *
     */
    @Override
    protected void draw() {
        // First
        from(Status.Submitted).to(
                Status.Rejected, Status.Acknowledged);

        // Somewhere
        from(Status.Acknowledged).to(
                Status.InProgressHeld,
                Status.InProgressPending,
                Status.Cancelled);       
        from(Status.InProgressHeld).to(
                Status.Resolved);
        from(Status.InProgressPending).to(
                Status.Cancelled,
                Status.Resolved);
        from(Status.Resolved).to(
                Status.Closed,
                Status.InProgressHeld,
                Status.InProgressPending);

        // Final
        from(Status.Closed);
        from(Status.Cancelled);
        from(Status.Rejected);
    }
}
