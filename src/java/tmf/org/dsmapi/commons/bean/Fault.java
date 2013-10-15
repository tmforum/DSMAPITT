package tmf.org.dsmapi.commons.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import tmf.org.dsmapi.commons.exceptions.ExceptionBean;

/**
 *
 * @author maig7313
 */
@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Fault {

    private ExceptionBean error;
    private String detail;    

    /**
     *
     * @param error
     */
    public Fault(ExceptionBean error) {
        this.error = error;
    }

    /**
     *
     * @param error
     * @param detail
     */
    public Fault(ExceptionBean error, String detail) {
        this.error = error;
        this.detail = detail;
    }   

    /**
     * @return the type
     */
    public ExceptionBean getError() {
        return error;
    }

    /**
     * @param error 
     */
    public void setError(ExceptionBean error) {
        this.error = error;
    }

    /**
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail the detail to set
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

}
