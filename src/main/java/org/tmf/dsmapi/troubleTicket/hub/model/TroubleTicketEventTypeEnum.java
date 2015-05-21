/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.troubleTicket.hub.model;

public enum TroubleTicketEventTypeEnum {

    InformationRequiredNotification("InformationRequiredNotification"),
    TicketChangedNotification("TicketChangedNotification"),
    TicketClearanceRequestNotification("TicketClearanceRequestNotification"),
    TicketStatusChangedNotification("TicketStatusChangedNotification");

    private String text;

    TroubleTicketEventTypeEnum(String text) {
        this.text = text;
    }

    /**
     *
     * @return
     */
    public String getText() {
        return this.text;
    }

    /**
     *
     * @param text
     * @return
     */
    public static org.tmf.dsmapi.troubleTicket.hub.model.TroubleTicketEventTypeEnum fromString(String text) {
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