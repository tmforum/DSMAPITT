/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.hub.model;

/**
 *
 * @author pierregauthier
 */
public enum EventTypeEnum {

    /**
     *
     */
    TicketCreateNotification("TicketCreateNotification"),
    /**
     *
     */
    TicketStatusChangedNotification("TicketStatusChangedNotification"),
    /**
     *
     */
    TicketChangedNotification("TicketChangedNotification"),
    /**
     *
     */
    TicketClearanceRequestNotification("TicketClearanceRequestNotification"),
    /**
     *
     */
    InformationRequiredNotification("InformationRequiredNotification");
    
    
    private String text;

    EventTypeEnum(String text) {
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
    public static org.tmf.dsmapi.hub.model.EventTypeEnum fromString(String text) {
        if (text != null) {
            for (EventTypeEnum b : EventTypeEnum.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}