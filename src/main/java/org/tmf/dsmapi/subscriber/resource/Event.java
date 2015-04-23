package org.tmf.dsmapi.subscriber.resource;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.subscriber.model.EventBag;
import org.tmf.dsmapi.subscriber.service.EventApiFacade;

@WebServlet("/subscriber/eventApi/event")
public class Event extends HttpServlet {

    @EJB
    EventApiFacade eventApiFacade;
    static final int COUNTER_KEY = 1;

    public Event() {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        EventBag notification = new EventBag();
        response.setContentType("application/json");
        notification.setDateEvent(new Date());
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

//        DataInputStream in = new DataInputStream(request.getInputStream());
//        byte[] buffer = new byte[256];
//        int bytesRead = 0;
//        while ((bytesRead = in.read(buffer)) != -1) {
//            byteOut.write(buffer, 0, bytesRead);
//        notification.setEvent(byteOut.toByteArray());
        BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line = null;
        StringBuilder responseData = new StringBuilder();
        
        System.out.println("Got event: \n");
        while ((line = in.readLine()) != null) {
            System.out.println(line);
            responseData.append(line);
        }
        notification.setEvent(responseData.toString().getBytes());

        try {
            eventApiFacade.create(notification);
            response.setStatus(201);
        } catch (BadUsageException ex) {
            Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500);
        } finally {
            byteOut.close();
            byteOut = null;
            //System.gc();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        response.setContentType("application/json");
        List<EventBag> notifications = eventApiFacade.findAll();
        ServletOutputStream servletOut = response.getOutputStream();
        servletOut.write("[".getBytes());
        for (int i = 0; notifications.size() > i; i++) {
            servletOut.write(notifications.get(i).getEvent());
            if (i + 1 < notifications.size()) {
                servletOut.write(",".getBytes());
            }
        }
        servletOut.write("]".getBytes());
        response.setStatus(200);
        servletOut.close();
        byteOut.close();
        byteOut = null;
        System.gc();
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<EventBag> notifications = eventApiFacade.findAll();
        eventApiFacade.removeAll();
        response.setStatus(200);
        response.getWriter().println(notifications.size() + " event(s) deleted");
    }
}
