package org.tmf.dsmapi.jaxrs.resource.admin;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.jaxrs.model.Report;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;
import org.tmf.dsmapi.troubleTicket.service.TroubleTicketFacade;
import org.tmf.dsmapi.troubleTicket.hub.model.TroubleTicketEvent;
import org.tmf.dsmapi.troubleTicket.hub.service.TroubleTicketEventFacade;
import org.tmf.dsmapi.troubleTicket.hub.service.TroubleTicketEventPublisherLocal;

@Stateless
@Path("admin")
public class TroubleTicketAdminResource {

    @EJB
    TroubleTicketFacade troubleTicketManagementFacade;
    @EJB
    TroubleTicketEventFacade eventFacade;
    @EJB
    TroubleTicketEventPublisherLocal publisher;

    @GET
    @Produces({"application/json"})
    @Path("")
    public List<TroubleTicket> findAll() {
        return troubleTicketManagementFacade.findAll();
    }

    /**
     *
     * For test purpose only
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

        int previousRows = troubleTicketManagementFacade.count();
        int affectedRows;

        // Try to persist entities
        try {
            affectedRows = troubleTicketManagementFacade.create(entities);
            for (TroubleTicket entitie : entities) {
                publisher.createNotification(entitie, "troubleTicketManagement created", new Date());
            }
        } catch (BadUsageException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        Report stat = new Report(troubleTicketManagementFacade.count());
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        // 201 OK
        return Response.created(null).
                entity(stat).
                build();
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response update(@PathParam("id") long id, TroubleTicket entity) throws UnknownResourceException {
        Response response = null;
        TroubleTicket troubleTicketManagement = troubleTicketManagementFacade.find(id);
        if (troubleTicketManagement != null) {
            entity.setId(id);
            troubleTicketManagementFacade.edit(entity);
            publisher.valueChangedNotification(entity, "TroubleTicket modified", new Date());
            // 201 OK + location
            response = Response.status(Response.Status.CREATED).entity(entity).build();

        } else {
            // 404 not found
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    /**
     *
     * For test purpose only
     * @return
     * @throws org.tmf.dsmapi.commons.exceptions.UnknownResourceException
     */
    @DELETE
    @Path("")
    public Report deleteAll() throws UnknownResourceException {

        eventFacade.removeAll();
        int previousRows = troubleTicketManagementFacade.count();
        troubleTicketManagementFacade.removeAll();
        List<TroubleTicket> pis = troubleTicketManagementFacade.findAll();
        for (TroubleTicket pi : pis) {
            delete(pi.getId());
        }

        int currentRows = troubleTicketManagementFacade.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    /**
     *
     * For test purpose only
     * @param id
     * @return
     * @throws UnknownResourceException
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) throws UnknownResourceException {
        try {
            int previousRows = troubleTicketManagementFacade.count();
            TroubleTicket entity = troubleTicketManagementFacade.find(id);

            // Event deletion
            publisher.deletionNotification(entity, "TroubleTicket Deleted", new Date());
            try {
                //Pause for 4 seconds to finish notification
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TroubleTicketAdminResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            // remove event(s) binding to the resource
            List<TroubleTicketEvent> events = eventFacade.findAll();
            for (TroubleTicketEvent event : events) {
                if (event.getResource().getId().equals(id)) {
                    eventFacade.remove(event.getId());
                }
            }
            //remove resource
            troubleTicketManagementFacade.remove(id);

            int affectedRows = 1;
            Report stat = new Report(troubleTicketManagementFacade.count());
            stat.setAffectedRows(affectedRows);
            stat.setPreviousRows(previousRows);

            // 200 
            Response response = Response.ok(stat).build();
            return response;
        } catch (UnknownResourceException ex) {
            Logger.getLogger(TroubleTicketAdminResource.class.getName()).log(Level.SEVERE, null, ex);
            Response response = Response.status(Response.Status.NOT_FOUND).build();
            return response;
        }
    }

    @GET
    @Produces({"application/json"})
    @Path("event")
    public List<TroubleTicketEvent> findAllEvents() {
        return eventFacade.findAll();
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
    public Response deleteEvent(@PathParam("id") String id) throws UnknownResourceException {

        int previousRows = eventFacade.count();
        List<TroubleTicketEvent> events = eventFacade.findAll();
        for (TroubleTicketEvent event : events) {
            if (event.getResource().getId().equals(id)) {
                eventFacade.remove(event.getId());

            }
        }
        int currentRows = eventFacade.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        // 200 
        Response response = Response.ok(stat).build();
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
        return new Report(troubleTicketManagementFacade.count());
    }
}
