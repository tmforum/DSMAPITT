package org.tmf.dsmapi.subscriber.resource;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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

@WebServlet("/subscriber/eventApi/current")
public class Current extends HttpServlet {

    @EJB
    EventApiFacade eventApiFacade;

    public Current() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);

        List<EventBag> events = eventApiFacade.findAll();
        ServletOutputStream servletOut = response.getOutputStream();
        EventBag mostRecentNotif = null;
        for (int i = 0; events.size() > i; i++) {
            mostRecentNotif = events.get(i);
            if (events.get(i).getDate().before(mostRecentNotif.getDate())) {
                mostRecentNotif = events.get(i);
            }
        }
        if (null != mostRecentNotif) {
            servletOut.write(mostRecentNotif.getEvent());
            response.setContentType("application/json");
            response.setStatus(200);
        } else {
            servletOut.write("no event found".getBytes());
            response.setContentType("application/text");
            response.setStatus(404);
        }
        out.flush();
        servletOut.close();
        byteOut.close();
        byteOut = null;
    }
}
