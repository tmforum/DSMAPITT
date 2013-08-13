/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt;

import java.io.Serializable;

/**
 *
 * @author pierregauthier
 * "relatedObjects": [
        {
            "involvement": "Disputed",
            "reference": "/invoice/1234"
        },
        {
            "involvement": "Adjusted",
            "reference": "/invoice/5678"
        }
 */
public class RelatedObject implements Serializable  {
    
    private String involvement;
    private String reference;

    public String getInvolvement() {
        return involvement;
    }

    public void setInvolvement(String involvement) {
        this.involvement = involvement;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
    
}
