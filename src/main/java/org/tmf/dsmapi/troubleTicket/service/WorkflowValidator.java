package org.tmf.dsmapi.troubleTicket.service;

import org.tmf.dsmapi.troubleTicket.model.Status;


public class WorkflowValidator {

    public static boolean isCorrect(Status current, Status next) {

        boolean valid = false;
        if(current.equals(next)) return true;

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
                    case InProgressHeld:
                    case InProgressPending:
                    case Cancelled:
                        valid = true;
                        break;
                }
                break;
            case InProgressHeld:
            case InProgressPending:
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
                    case InProgressHeld:
                    case InProgressPending:
                        valid = true;
                        break;
                }
                break;
        }
        return valid;
    }

}
