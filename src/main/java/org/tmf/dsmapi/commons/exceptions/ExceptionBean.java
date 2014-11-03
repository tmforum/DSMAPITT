package org.tmf.dsmapi.commons.exceptions;

public class ExceptionBean {
    
    private String code;
    private String title;
    
    public ExceptionBean(String code, String title) {
        this.code=code;
        this.title=title;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
}
