package org.tmf.dsmapi.commons.workflow;

import java.io.Serializable;
import java.util.EnumSet;
import static java.util.EnumSet.noneOf;

/**
 *
 * @author maig7313
 * @param <E>
 */
public class Transition<E extends Enum<E>> implements Serializable, Cloneable{

    private E from;
    private EnumSet<E> to;
    
    /**
     *
     */
    public Transition () {
    }    
    
    /**
     *
     * @param label
     */
    public Transition (E label) {
        this.from = label;
    }   
    
    /**
     *
     * @param e1
     * @return
     */
    public Transition to(E e1) {
        to = noneOf(e1.getDeclaringClass());
        to.add(e1);
        return this;
    }
    
    /**
     *
     * @param e1
     * @param e2
     * @return
     */
    public Transition to(E e1, E e2) {
        to = noneOf(e1.getDeclaringClass());        
        to.add(e1);
        to.add(e2);
        return this;        
    }
    
    /**
     *
     * @param e1
     * @param e2
     * @param e3
     * @return
     */
    public Transition to(E e1, E e2, E e3) {
        to = noneOf(e1.getDeclaringClass());        
        to.add(e1);
        to.add(e2);
        to.add(e3);
        return this;        
    }
    
    /**
     *
     * @param e1
     * @param e2
     * @param e3
     * @param e4
     * @return
     */
    public Transition to(E e1, E e2, E e3, E e4) {
        to = noneOf(e1.getDeclaringClass());        
        to.add(e1);
        to.add(e2);
        to.add(e3);
        to.add(e4); 
        return this;        
    }
    
    /**
     *
     * @param e1
     * @param e2
     * @param e3
     * @param e4
     * @param e5
     * @return
     */
    public Transition to(E e1, E e2, E e3, E e4, E e5) {
        to = noneOf(e1.getDeclaringClass());        
        to.add(e1);
        to.add(e2);
        to.add(e3);
        to.add(e4);
        to.add(e5);  
        return this;        
    }

    /**
     *
     * @param e1
     * @return
     */
    public boolean isAnAuthorizedTransition(E e1) {
        return (to!=null && to.contains(e1));
    }

    /**
     *
     * @return
     */
    public E getFrom() {
        return from;
    }

    /**
     *
     * @return
     */
    public EnumSet<E> getTo() {
        return to;
    }
    
    /**
     *
     * @return
     */
    public boolean isFinal() {
        return ((to == null ) || ( to.isEmpty()));
    }       

}
