package org.tmf.dsmapi.jaxrs.resource;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.tmf.dsmapi.troubleTicket.model.Report;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.hub.model.Event;
import org.tmf.dsmapi.hub.service.EventFacade;
import org.tmf.dsmapi.hub.service.EventPublisherLocal;
import org.tmf.dsmapi.hub.service.HubFacade;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;
import org.tmf.dsmapi.troubleTicket.service.TroubleTicketFacade;

/**
 *
 * @author maig7313
 */
@Stateless
@Path("admin")
public class AdminResource {

    @EJB
    TroubleTicketFacade troubleTicketFacade;
    @EJB
    EventFacade eventFacade;
    @EJB
    HubFacade hubFacade;
    @EJB
    EventPublisherLocal publisher;

    /**
     *
     * @param entities
     * @return
     */
    @POST
    @Path("")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response post(List<TroubleTicket> entities) {

        if (entities == null) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        int previousRows = troubleTicketFacade.count();
        int affectedRows;

        // Try to persist entities
        try {
            affectedRows = troubleTicketFacade.create(entities);
        } catch (BadUsageException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        Report stat = new Report(troubleTicketFacade.count());
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        // 201 OK
        return Response.created(null).
                entity(stat).
                build();
    }

    /**
     *
     * @return
     * @throws tmf.org.dsmapi.commons.exceptions.UnknownResourceException
     */
    @DELETE
    @Path("")
    public Report deleteAll() throws UnknownResourceException {

        eventFacade.removeAll();
        int previousRows = troubleTicketFacade.count();
        troubleTicketFacade.removeAll();
        List<TroubleTicket> tts = troubleTicketFacade.findAll();
        for (TroubleTicket tt : tts) {
            delete(tt.getId());
        }
        int currentRows = troubleTicketFacade.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    /**
     *
     * @param id
     * @return
     * @throws UnknownResourceException
     */
    @DELETE
    @Path("{id}")
//    public Report delete(@PathParam("id") String id) throws UnknownResourceException {
    public Response delete(@PathParam("id") String id) {
        try {
            // int previousRows = troubleTicketFacade.count();
            TroubleTicket entity = troubleTicketFacade.find(id);

            try {
                //Pause for 4 seconds to finish notification
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AdminResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            // remove event(s) binding to the resource
            List<Event> events = eventFacade.findAll();
            for (Event event : events) {
                if (event.getResource().getId().equals(id)) {
                    eventFacade.remove(event.getId());
                }
            }
            //remove resource
            troubleTicketFacade.remove(id);

            //        int affectedRows = 1;
            //        Report stat = new Report(troubleTicketFacade.count());
            //        stat.setAffectedRows(affectedRows);
            //        stat.setPreviousRows(previousRows);
            //        return stat;
            // 200 
            Response response = Response.ok(entity).build();
            return response;
        } catch (UnknownResourceException ex) {
            Logger.getLogger(AdminResource.class.getName()).log(Level.SEVERE, null, ex);
            Response response = Response.status(Response.Status.NOT_FOUND).build();
            return response;
        }
    }

    /**
     *
     * @return
     */
    @DELETE
    @Path("hub")
    public Report deleteAllHub() {

        int previousRows = hubFacade.count();
        hubFacade.removeAll();
        int currentRows = hubFacade.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    @DELETE
    @Path("event")
    public Report deleteAllEvent() {

        int previousRows = eventFacade.count();
        eventFacade.removeAll();
        int currentRows = eventFacade.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    @DELETE
    @Path("event/{id}")
//    public Report deleteEvent(@PathParam("id") String id) throws UnknownResourceException {
    public Response deleteEvent(@PathParam("id") String id) throws UnknownResourceException {

//        int previousRows = eventFacade.count();
        List<Event> events = eventFacade.findAll();
        Event oneEvent = null;
        for (Event event : events) {
            if (event.getResource().getId().equals(id)) {
                oneEvent = event;
                eventFacade.remove(event.getId());

            }
        }
//        int currentRows = eventFacade.count();
//        int affectedRows = previousRows - currentRows;
//
//        Report stat = new Report(currentRows);
//        stat.setAffectedRows(affectedRows);
//        stat.setPreviousRows(previousRows);
//
//        return stat;
        // 200 
        Response response = Response.ok(oneEvent).build();
        return response;
    }

    /**
     *
     * @return
     */
    @GET
    @Path("count")
    @Produces({"application/json"})
    public Report count() {
        return new Report(troubleTicketFacade.count());
    }
//    /**
//     *
//     */
//    @DELETE
//    @Path("troubleTicket/cache")
//    public void clearCache() {
//        troubleTicketFacade.clearCache();
//    }
//
//    @PUT
//    @Path("troubleTicket/wf/delay/{value}")
//    public void patchDelay(@PathParam("value") long value) {
//        troubleTicketFacade.setDelay(value);
//    }
}
