/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt;

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
