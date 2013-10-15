package tmf.org.dsmapi.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author maig7313
 */
public class TMFDate {

    private static SimpleDateFormat FORMATER;

    /**
     *
     * @param date
     * @return
     */
    public static String toString(Date date) {
        return getFormater().format(date);
    }
    
    /**
     *
     * @param date
     * @return
     */
    public static Date parse(String value) {
        try {
            return getFormater().parse(value);
        } catch (ParseException ex) {
            return null;
        }
    }    

    private static SimpleDateFormat getFormater() {
        if (FORMATER == null) {
            FORMATER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
            TimeZone tz = TimeZone.getTimeZone("UTC");
            FORMATER.setTimeZone(tz);
        }
        return FORMATER;
    }
}
