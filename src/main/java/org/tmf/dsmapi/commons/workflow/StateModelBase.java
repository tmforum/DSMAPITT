package org.tmf.dsmapi.commons.workflow;

import java.util.EnumMap;
import java.util.Map;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.ExceptionType;

/**
 *
 * @param <E> 
 * @author maig7313
 */
public abstract class StateModelBase<E extends Enum<E>> implements StateModel<E> {

    private Map<E, Transition> transitionMap;
    private Transition<E> firstTransition;
    private Class<E> type;

    /**
     *
     * @param type
     */
    public StateModelBase(Class<E> type) {
        this.type = type;
    }

    private Transition<E> getTransition(E from) throws BadUsageException {
        Transition<E> transition;
        if (from == null) {
            transition = firstTransition;
        } else {
            if (getTransitionMap().containsKey(from)) {
                transition = this.getTransitionMap().get(from);
            } else {
                throw new BadUsageException(ExceptionType.BAD_USAGE_FLOW_UNKNOWN_STATE, this.getType().getSimpleName() + "=" + from);
            }
        }
        return transition;
    }

    /**
     * @return first currentT
     */
    @Override
    public Transition<E> getFirstTransition() {
        if (firstTransition == null) {
            draw();
        }
        return firstTransition;
    }

    /**
     *
     * @param from
     * @param to
     * @throws BadUsageException
     */
    @Override
    public void checkTransition(E from, E to) throws BadUsageException {
        if (firstTransition == null) {
            draw();
        }
        Transition<E> transition = getTransition(from);
        if (transition.isFinal()) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_FLOW_TRANSITION, "item is in final state: " + from.toString());
        }
        if (!transition.isAnAuthorizedTransition(to)) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_FLOW_TRANSITION, "authorized: " + transition.getTo().toString());
        }

    }

    /**
     *
     * @param e1
     * @return
     */
    protected Transition from(E e1) {
        Transition transition;
        if (!getTransitionMap().containsKey(e1)) {
            transition = new Transition(e1);
            getTransitionMap().put(e1, transition);
        } else {
            transition = getTransitionMap().get(e1);
        }
        return transition;
    }

    /**
     *
     * @return
     */
    protected Transition fromFirst() {
        firstTransition = new Transition();
        return firstTransition;
    }
    
    /**
     *
     * @param e1
     * @return
     */
    protected Transition fromFirst(E e1) {
        firstTransition = from(e1);
        return firstTransition;
    }    

    /**
     *
     */
    protected abstract void draw();

    /**
     * @return the transitionMap
     */
    public Map<E, Transition> getTransitionMap() {
        if (transitionMap == null) {
            transitionMap = new EnumMap<E, Transition>(getType());
        }
        return transitionMap;
    }

    /**
     * @return the enum type
     */
    private Class<E> getType() {
        return this.type;
    }
}
