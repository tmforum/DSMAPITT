package tmf.org.dsmapi.tt.facade;

import tmf.org.dsmapi.tt.model.Status;
import static tmf.org.dsmapi.tt.model.Status.Acknowledged;
import static tmf.org.dsmapi.tt.model.Status.Cancelled;
import static tmf.org.dsmapi.tt.model.Status.Rejected;
import static tmf.org.dsmapi.tt.model.Status.Resolved;
import static tmf.org.dsmapi.tt.model.Status.Submitted;

public class WorkflowValidator {

    public static boolean isCorrect(Status current, Status next) {

        boolean valid = false;

        switch (current) {
            case Submitted:
                switch (next) {
                    case Rejected:
                    case Acknowledged:
                        valid = true;
                        break;
                }
                break;
            case Acknowledged:
                switch (next) {
                    case InProgress_Held:
                    case InProgress_Pending:
                    case Cancelled:
                        valid = true;
                        break;
                }
                break;
            case InProgress_Held:
            case InProgress_Pending:
                switch (next) {
                    case Resolved:
                    case Cancelled:
                        valid = true;
                        break;
                }
                break;
            case Resolved:
                switch (next) {
                    case Closed:
                    case InProgress_Held:
                    case InProgress_Pending:
                        valid = true;
                        break;
                }
                break;
        }
        return valid;
    }

}
