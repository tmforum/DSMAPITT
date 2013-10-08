package tmf.org.dsmapi.tt.workflow;

import java.io.Serializable;
import java.util.EnumSet;
import static java.util.EnumSet.noneOf;

public class Transition<E extends Enum<E>> implements Serializable, Cloneable{

    private E from;
    private EnumSet<E> to;
    
    public Transition () {
    }    
    
    public Transition (E label) {
        this.from = label;
    }   
    
    public Transition to(E e1) {
        to = noneOf(e1.getDeclaringClass());
        to.add(e1);
        return this;
    }
    
    public Transition to(E e1, E e2) {
        to = noneOf(e1.getDeclaringClass());        
        to.add(e1);
        to.add(e2);
        return this;        
    }
    
    public Transition to(E e1, E e2, E e3) {
        to = noneOf(e1.getDeclaringClass());        
        to.add(e1);
        to.add(e2);
        to.add(e3);
        return this;        
    }
    
    public Transition to(E e1, E e2, E e3, E e4) {
        to = noneOf(e1.getDeclaringClass());        
        to.add(e1);
        to.add(e2);
        to.add(e3);
        to.add(e4); 
        return this;        
    }
    
    public Transition to(E e1, E e2, E e3, E e4, E e5) {
        to = noneOf(e1.getDeclaringClass());        
        to.add(e1);
        to.add(e2);
        to.add(e3);
        to.add(e4);
        to.add(e5);  
        return this;        
    }

    public boolean isAnAuthorizedTransition(E e1) {
        return (to!=null && to.contains(e1));
    }

    public E getFrom() {
        return from;
    }

    public EnumSet<E> getTo() {
        return to;
    }
    
    public boolean isFinal() {
        return ((to == null ) || ( to.isEmpty()));
    }       

}
