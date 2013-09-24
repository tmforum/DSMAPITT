package tmf.org.dsmapi.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Format {

    public static String toString(Date date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        TimeZone tz = TimeZone.getTimeZone("UTC");

        df.setTimeZone(tz);

        String output = df.format(date);

        return output;
        /*
         int inset0 = 9;
         int inset1 = 6;
        
         String s0 = output.substring( 0, output.length() - inset0 );
         String s1 = output.substring( output.length() - inset1, output.length() );

         String result = s0 + s1;

         result = result.replaceAll( "UTC", "+00:00" ); 
        
         return result; */

    }
    
}
