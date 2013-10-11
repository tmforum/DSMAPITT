package tmf.org.dsmapi.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author maig7313
 */
public class Format {

    /**
     *
     * @param date
     * @return
     */
    public static String toString(Date date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        TimeZone tz = TimeZone.getTimeZone("UTC");

        df.setTimeZone(tz);

        String output = df.format(date);

        return output;
    }
    
}
