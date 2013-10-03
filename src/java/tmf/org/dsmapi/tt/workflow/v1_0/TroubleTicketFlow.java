package tmf.org.dsmapi.tt.workflow.v1_0;

import tmf.org.dsmapi.tt.Status;
import tmf.org.dsmapi.tt.workflow.BaseFlow;

/**
 *
 * @author maig7313
 */
public class TroubleTicketFlow extends BaseFlow<Status> {

    public TroubleTicketFlow() {
        // The functional enum, here the TT Status, used to build EnumMap
        super(Status.class);
    }

    @Override
    public String getURN() {
        return "troubleTicket/1.0";
    }

    @Override
    protected void draw() {
        // First
        fromFirst().to(
                Status.Rejected,
                Status.Acknowledged);

        // Somewhere
        from(Status.Acknowledged).to(
                Status.InProgress,                
                Status.Cancelled);
        from(Status.InProgress).to(
                Status.InProgress_Held,
                Status.InProgress_Pending,
                Status.Resolved,
                Status.Cancelled);        
        from(Status.InProgress_Held).to(
                Status.InProgress);
        from(Status.InProgress_Pending).to(
                Status.InProgress);
        from(Status.Resolved).to(
                Status.Closed,
                Status.InProgress);

        // Final
        from(Status.Closed);
        from(Status.Rejected);
        from(Status.Cancelled);
    }
}
