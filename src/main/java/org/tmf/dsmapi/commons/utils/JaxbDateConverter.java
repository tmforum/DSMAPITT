/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.commons.utils;

import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author OTRE7276
 */
public class JaxbDateConverter {

    public JaxbDateConverter() {
    }

    public static Date parseDate(String s) {
        if (s == null) {
            return null;
        }
        return DatatypeConverter.parseDate(s).getTime();
    }

    public static String printDate(Date dt) {
        if (dt == null) {
            return null;
        }
        
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        return DatatypeConverter.printDate(c);
    }
    
    public static Date parseDateTime(String s) {
        if (s == null) {
            return null;
        }
        return DatatypeConverter.parseDateTime(s).getTime();
    }
    
    public static String printDateTime(Date dt) {
        if (dt == null) {
            return null;
        }
        
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        return DatatypeConverter.printDateTime(c);
    }
}
