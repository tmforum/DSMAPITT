package tmf.org.dsmapi.commons.exceptions;

import java.io.Serializable;
import tmf.org.dsmapi.tt.JsonError;

/**
 * generic class for all functional exceptions. Functional exceptions must be
 * checked exceptions
 */
public class FunctionalException extends Exception implements Serializable {

    private static final long serialVersionUID = 7552671441723224932L;
    private String localisationClass;
    private String localisationMethod;
    
    private JsonError error;

    public FunctionalException() {
        super();
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
     * @return the error
     */
    public JsonError getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(JsonError error) {
        this.error = error;
    }
}
