package org.tmf.dsmapi.subscriber.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tmf.dsmapi.subscriber.model.EventBag;
import org.tmf.dsmapi.subscriber.service.EventApiFacade;

@WebServlet("/subscriber/api/history")
public class History extends HttpServlet {

    @EJB
    EventApiFacade eventApiFacade;

    public History() {
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
        response.getWriter().println(notifications.size()+" event(s) deleted");
    }
    
}
