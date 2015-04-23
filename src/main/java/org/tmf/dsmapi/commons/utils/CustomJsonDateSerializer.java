/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.commons.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 *
 * @author ecus6396
 */
public class CustomJsonDateSerializer extends JsonSerializer<Date>
{
    @Override
    public void serialize(Date aDate, JsonGenerator aJsonGenerator, SerializerProvider aSerializerProvider)
            throws IOException, JsonProcessingException {
//         "2014-04-25T12:16:43.397+0000"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        "2013-07-21 08:16:39ZGMT+1"
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'zzz");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        String dateString = dateFormat.format(aDate);
        aJsonGenerator.writeString(dateString);
    }
}