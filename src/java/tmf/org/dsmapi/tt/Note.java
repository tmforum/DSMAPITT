/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt;

import java.io.Serializable;
import javax.persistence.Embeddable;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author pierregauthier "notes": [ { "date": "2013-07-19 09:55:30", "author":
 * "Arthur Evans", "text": "Already called the expert" }, { "date": "2013-07-21
 * 08:55:12", "author": "Arthur Evans", "text": "Informed the originator" }
 */
@Embeddable
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Note implements Serializable {

    private String date;
    private String author;
    private String text;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
