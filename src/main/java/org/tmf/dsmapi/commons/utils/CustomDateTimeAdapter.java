/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author ecus6396
 */
public class CustomDateTimeAdapter extends XmlAdapter<String, Date> {

//    @Override
//    public String marshal(Date v) throws Exception {
//        return JaxbDateConverter.printDateTime(v);
//    }
//
//    @Override
//    public Date unmarshal(String v) throws Exception {
//        return JaxbDateConverter.parseDateTime(v);
//    }
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssX");

    @Override
    public String marshal(Date date) throws Exception {
        return dateFormat.format(date);
    }

    @Override
    public Date unmarshal(String date) throws Exception {
        return dateFormat.parse(date);
    }
}