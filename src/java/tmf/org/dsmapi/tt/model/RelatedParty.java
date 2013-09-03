/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.model;

import java.io.Serializable;

/**
 *
 * @author pierregauthier
 * "relatedParties": [
        {
            "role": "Originator",
            "reference": "/customer/1234"
        },
        {
            "role": "Owner",
            "reference": "/operator/1234"
        },
        {
            "role": "Reviser",
            "reference": "Roger Collins"
        }
 */
public class RelatedParty implements Serializable {
    
    private String role;
    private String reference;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
    
}
