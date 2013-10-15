/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.commons.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author maig7313
 */
@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Delay {
    
    private long delay;

    /**
     * @return the delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * @param delay the delay to set
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }
    
}
