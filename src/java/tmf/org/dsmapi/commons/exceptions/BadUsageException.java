package tmf.org.dsmapi.commons.exceptions;

/**
 *
 * @author maig7313
 */
public class BadUsageException extends FunctionalException {
    
    /**
     *
     * @param type
     */
    public BadUsageException(ExceptionType type) {
        super(type);
    }
    
    /**
     *
     * @param type
     * @param message
     */
    public BadUsageException(ExceptionType type, String message) {
        super(type, message);
    }     
    
}
