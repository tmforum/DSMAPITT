package tmf.org.dsmapi.commons.exceptions;

import java.io.Serializable;

/**
 * generic class for all functional exceptions. Functional exceptions must be
 * checked exceptions
 */
public class TechnicalException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 7552671441723224932L;
    private String localisationClass;
    private String localisationMethod;
    private ExceptionType type = ExceptionType.TECHNICAL;


    public TechnicalException() {
        super();
        localisationClass = "";
        localisationMethod = "";
    }   

    public TechnicalException(String message) {
        super(message);
        localisationClass = "";
        localisationMethod = "";
    }

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
        localisationClass = "";
        localisationMethod = "";
    }

    public TechnicalException(Throwable cause) {
        super(cause);
        localisationClass = "";
        localisationMethod = "";
    }

    public TechnicalException(String clazz, String method, String message) {
        super(message);
        localisationClass = clazz;
        localisationMethod = method;
    }

    public TechnicalException(String clazz, String method, String message, Throwable cause) {
        super(message, cause);
        localisationClass = clazz;
        localisationMethod = method;
    }

    public String getLocalisationClasse() {
        return localisationClass;
    }

    public String getLocalisationMethod() {
        return localisationMethod;
    }

    public String getLocalisation() {
        return localisationClass + "." + localisationMethod;
    }

    /**
     * @return the type
     */
    public ExceptionType getType() {
        return type;
    }
      
}
