package tmf.org.dsmapi.tt;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JsonError {
    
    private String code;
    private String message;
    
    public JsonError(String code, String message) {
        this.code=code;
        this.message=message;
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
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
}
