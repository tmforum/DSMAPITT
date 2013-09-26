/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt;

/**
 *
 * @author pierregauthier
 */
public enum Severity {

    Low,
    Critical,
    Medium,
    High;  //; is optional

    public static Severity fromString(String text) {
        if (text != null) {
            for (Severity b : Severity.values()) {
                if (text.equalsIgnoreCase(b.toString())) {
                    return b;
                }
            }
        }
        return null;
    }
}
