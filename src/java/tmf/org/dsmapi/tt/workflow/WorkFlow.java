/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.workflow;

import javax.ejb.Local;

/**
 *
 * @param <T> 
 * @author pierregauthier
 */
@Local
public interface WorkFlow<T> {
    
    /**
     *
     * @param object
     */
    void start(T object);

    /**
     *
     * @param object
     */
    void wakeUp(T object);
    
}
