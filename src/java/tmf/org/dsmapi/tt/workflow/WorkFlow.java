/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.workflow;

import javax.ejb.Local;

/**
 *
 * @author pierregauthier
 */
@Local
public interface WorkFlow<T> {
    
    void start(T object);

    void wakeUp(T object);
    
}
