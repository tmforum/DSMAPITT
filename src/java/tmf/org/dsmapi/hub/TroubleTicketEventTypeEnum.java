/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub;

/**
 *
 * @author pierregauthier
 */
public enum TroubleTicketEventTypeEnum {

    TicketCreateNotification("TicketCreateNotification"),
    TicketStatusChangedNotification("TicketStatusChangedNotification"),
    TicketChangedNotification("TicketStatusChangedNotification"),
    TicketClearanceRequestNotification("TicketClearanceRequestNotification"),
    InformationRequiredNotification("InformationRequiredNotification");
    
    
    private String text;

    TroubleTicketEventTypeEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static tmf.org.dsmapi.hub.TroubleTicketEventTypeEnum fromString(String text) {
        if (text != null) {
            for (TroubleTicketEventTypeEnum b : TroubleTicketEventTypeEnum.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}