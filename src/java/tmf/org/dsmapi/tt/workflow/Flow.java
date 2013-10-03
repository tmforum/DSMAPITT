package tmf.org.dsmapi.tt.workflow;

import tmf.org.dsmapi.commons.exceptions.BadUsageException;

/**
 *
 * @author maig7313
 */
public interface Flow<T extends Enum<T>> {
    
    public void checkTransition(T from, T to) throws BadUsageException;
    
    public Transition<T> getFirstTransition();
    
}
