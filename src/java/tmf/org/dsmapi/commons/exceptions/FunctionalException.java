package tmf.org.dsmapi.commons.exceptions;

import java.io.Serializable;

/**
 * generic class for all functional exceptions. Functional exceptions must be
 * checked exceptions
 */
public class FunctionalException extends Exception implements Serializable {

    private static final long serialVersionUID = 7552671441723224932L;
    private String localisationClass;
    private String localisationMethod;
    private ExceptionType type;


    public FunctionalException() {
        super();
        localisationClass = "";
        localisationMethod = "";
    }
    
    public FunctionalException(ExceptionType type) {
        super();
        this.type = type;
        localisationClass = "";
        localisationMethod = "";
    }
    
    public FunctionalException(ExceptionType type, String message) {
        super(message);
        this.type = type;
        localisationClass = "";
        localisationMethod = "";
    }    

    public FunctionalException(String message) {
        super(message);
        localisationClass = "";
        localisationMethod = "";
    }

    public FunctionalException(String message, Throwable cause) {
        super(message, cause);
        localisationClass = "";
        localisationMethod = "";
    }

    public FunctionalException(Throwable cause) {
        super(cause);
        localisationClass = "";
        localisationMethod = "";
    }

    public FunctionalException(String clazz, String method, String message) {
        super(message);
        localisationClass = clazz;
        localisationMethod = method;
    }

    public FunctionalException(String clazz, String method, String message, Throwable cause) {
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

    /**
     * @param type the type to set
     */
    public void setType(ExceptionType type) {
        this.type = type;
    }
      
}
