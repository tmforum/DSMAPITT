package tmf.org.dsmapi.tt.service;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import tmf.org.dsmapi.commons.exceptions.ExceptionBean;

@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class JsonFault {

    private ExceptionBean error;
    private String detail;

    public JsonFault(ExceptionBean error) {
        this.error = error;
    }

    public JsonFault(ExceptionBean error, String detail) {
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
     * @param type the type to set
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
