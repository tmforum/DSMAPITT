/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.troubleTicket.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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

@Embeddable
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RelatedObject implements Serializable  {
    
    private String reference;
    private String involvement;    

    /**
     *
     * @return
     */
    public String getInvolvement() {
        return involvement;
    }

    /**
     *
     * @param involvement
     */
    public void setInvolvement(String involvement) {
        this.involvement = involvement;
    }

    /**
     *
     * @return
     */
    public String getReference() {
        return reference;
    }

    /**
     *
     * @param reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }
    
}
