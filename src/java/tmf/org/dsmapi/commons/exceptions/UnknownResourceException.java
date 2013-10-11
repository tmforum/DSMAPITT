package tmf.org.dsmapi.commons.exceptions;

/**
 *
 * @author maig7313
 */
public class UnknownResourceException extends FunctionalException {
    
    /**
     *
     * @param type
     */
    public UnknownResourceException(ExceptionType type) {
        super(type);
    }
    
    /**
     *
     * @param type
     * @param message
     */
    public UnknownResourceException(ExceptionType type, String message) {
        super(type, message);
    }     
    
}
