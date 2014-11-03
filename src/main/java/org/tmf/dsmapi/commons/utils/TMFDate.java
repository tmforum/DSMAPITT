package org.tmf.dsmapi.commons.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 *
 * @author maig7313
 */
public class TMFDate extends JsonSerializer<Date>{

    private static SimpleDateFormat FORMATER;
    private static String label="yyyy-MM-dd'T'HH:mm:ssz";

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
//            FORMATER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
            FORMATER = new SimpleDateFormat(label);
            TimeZone tz = TimeZone.getTimeZone("UTC");
            FORMATER.setTimeZone(tz);
        }
        return FORMATER;
    }

    @Override
    public void serialize(Date t, JsonGenerator jg, SerializerProvider sp) 
            throws IOException, JsonProcessingException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(label);
//        "2013-07-21 08:16:39ZGMT+1"
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'zzz");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        String dateString = dateFormat.format(t);
        jg.writeString(dateString);
    }
}
