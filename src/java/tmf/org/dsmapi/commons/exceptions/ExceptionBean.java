package tmf.org.dsmapi.commons.exceptions;

/**
 *
 * @author maig7313
 */
public class ExceptionBean {
    
    private String code;
    private String title;
    
    /**
     *
     * @param code
     * @param title
     */
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
