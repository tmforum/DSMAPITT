/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.troubleTicket.model;

/**
 *
 * @author pierregauthier TMFORUM
 */
public enum Status {

    /**
     *
     */
    Submitted,
    /**
     *
     */
    Acknowledged,
    /**
     *
     */
    InProgress,
    /**
     *
     */
    InProgress_Held,
    /**
     *
     */
    InProgress_Pending,
    /**
     *
     */
    Resolved,
    /**
     *
     */
    Closed,
    /**
     *
     */
    Rejected,
    /**
     *
     */
    Cancelled;  //; is optional

    /**
     *
     * @param text
     * @return
     */
    public static Status fromString(String text) {
        if (text != null) {
            for (Status b : Status.values()) {
                if (text.equalsIgnoreCase(b.toString())) {
                    return b;
                }
            }
        }
        return null;
    }
}
