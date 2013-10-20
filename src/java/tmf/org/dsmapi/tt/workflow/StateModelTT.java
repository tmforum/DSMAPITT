package tmf.org.dsmapi.tt.workflow;

import tmf.org.dsmapi.tt.Status;

/**
 *
 * @author maig7313
 */
public class StateModelTT extends StateModelBase<Status> {
    
    /**
     *
     */
    public StateModelTT() {
        // Use troubleTicket Status
        super(Status.class);
    }    

    /**
     *
     */
    @Override
    protected void draw() {
        // First
        fromFirst(Status.Submitted).to(
                Status.Rejected,
                Status.Acknowledged);

        // Somewhere
        from(Status.Acknowledged).to(
                Status.InProgress, 
                Status.InProgress_Held,
                Status.InProgress_Pending,
                Status.Cancelled);
        from(Status.InProgress).to(
                Status.InProgress_Held,
                Status.InProgress_Pending,
                Status.Resolved,
                Status.Cancelled);       
        from(Status.InProgress_Held).to(
                Status.InProgress,
                Status.InProgress_Pending,
                Status.Resolved,
                Status.Cancelled);
        from(Status.InProgress_Pending).to(
                Status.InProgress,
                Status.InProgress_Held,
                Status.Resolved,
                Status.Cancelled);
        from(Status.Resolved).to(
                Status.Closed,
                Status.InProgress,
                Status.InProgress_Held,
                Status.InProgress_Pending);

        // Final
        from(Status.Closed);
        from(Status.Rejected);
        from(Status.Cancelled);
    }
}
