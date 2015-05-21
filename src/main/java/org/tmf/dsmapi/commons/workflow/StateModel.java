package org.tmf.dsmapi.commons.workflow;

import org.tmf.dsmapi.commons.exceptions.BadUsageException;

/**
 *
 * @param <T> 
 * @author maig7313
 */
public interface StateModel<T extends Enum<T>> {
    
    /**
     *
     * @param from
     * @param to
     * @throws BadUsageException
     */
    public void checkTransition(T from, T to) throws BadUsageException;
    
    /**
     *
     * @return
     */
    public Transition<T> getFirstTransition();    
    
}
