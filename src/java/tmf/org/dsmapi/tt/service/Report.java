package tmf.org.dsmapi.tt.service;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Report {

    private String previousRows;
    private String currentRows;
    private String affectedRows;
    
    /**
     *
     */
    public Report () {
    }    
    
    /**
     *
     * @param currentRows
     */
    public Report (int currentRows) {
        this.currentRows=String.valueOf(currentRows);
    }

    /**
     * @return the currentRows
     */
    public String getCurrentRows() {
        return currentRows;
    }

    /**
     * @param currentRows the currentRows to set
     */
    public void setCurrentRows(int currentRows) {
        this.currentRows = String.valueOf(currentRows);
    }

    /**
     * @return the previousRows
     */
    public String getPreviousRows() {
        return previousRows;
    }

    /**
     * @param previousRows the previousRows to set
     */
    public void setPreviousRows(int previousRows) {
        this.previousRows = String.valueOf(previousRows);
    }

    /**
     * @return the affectedRows
     */
    public String getAffectedRows() {
        return affectedRows;
    }

    /**
     * @param affectedRows the affectedRows to set
     */
    public void setAffectedRows(int affectedRows) {
        this.affectedRows = String.valueOf(affectedRows);
    }
    
}
