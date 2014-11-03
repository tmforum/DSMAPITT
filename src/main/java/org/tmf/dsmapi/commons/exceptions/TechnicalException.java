package org.tmf.dsmapi.commons.exceptions;

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


    /**
     *
     */
    public TechnicalException() {
        super();
        localisationClass = "";
        localisationMethod = "";
    }   

    /**
     *
     * @param message
     */
    public TechnicalException(String message) {
        super(message);
        localisationClass = "";
        localisationMethod = "";
    }

    /**
     *
     * @param message
     * @param cause
     */
    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
        localisationClass = "";
        localisationMethod = "";
    }

    /**
     *
     * @param cause
     */
    public TechnicalException(Throwable cause) {
        super(cause);
        localisationClass = "";
        localisationMethod = "";
    }

    /**
     *
     * @param clazz
     * @param method
     * @param message
     */
    public TechnicalException(String clazz, String method, String message) {
        super(message);
        localisationClass = clazz;
        localisationMethod = method;
    }

    /**
     *
     * @param clazz
     * @param method
     * @param message
     * @param cause
     */
    public TechnicalException(String clazz, String method, String message, Throwable cause) {
        super(message, cause);
        localisationClass = clazz;
        localisationMethod = method;
    }

    /**
     *
     * @return
     */
    public String getLocalisationClasse() {
        return localisationClass;
    }

    /**
     *
     * @return
     */
    public String getLocalisationMethod() {
        return localisationMethod;
    }

    /**
     *
     * @return
     */
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
