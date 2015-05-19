package org.tmf.dsmapi.jaxrs.resource.admin;

import java.util.ArrayList;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.jaxrs.model.Report;
import org.tmf.dsmapi.commons.utils.TMFDate;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;
import org.tmf.dsmapi.troubleTicket.service.TroubleTicketFacade;
import org.tmf.dsmapi.troubleTicket.hub.model.TroubleTicketEvent;
import org.tmf.dsmapi.troubleTicket.hub.service.TroubleTicketEventFacade;
import org.tmf.dsmapi.troubleTicket.hub.service.TroubleTicketEventPublisherLocal;
import org.tmf.dsmapi.troubleTicket.model.Note;
import org.tmf.dsmapi.troubleTicket.model.RelatedObject;
import org.tmf.dsmapi.troubleTicket.model.RelatedParty;
import org.tmf.dsmapi.troubleTicket.model.Status;

@Stateless
@Path("admin/troubleTicket")
public class TroubleTicketAdminResource {

    @EJB
    TroubleTicketFacade troubleTicketManagementFacade;
    @EJB
    TroubleTicketEventFacade eventFacade;
//    @EJB
//    TroubleTicketEventPublisherLocal publisher;

    @GET
    @Produces({"application/json"})
    @Path("")
    public List<TroubleTicket> findAll() {
        return troubleTicketManagementFacade.findAll();
    }

    /**
     *
     * For test purpose only
     *
     * @param entities
     * @return
     */
    @POST
    @Path("")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response post(List<TroubleTicket> entities, @Context UriInfo info) throws UnknownResourceException {

        if (entities == null) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        int previousRows = troubleTicketManagementFacade.count();
        int affectedRows=0;

        // Try to persist entities
        try {
            for (TroubleTicket entitie : entities) {
                troubleTicketManagementFacade.create(entitie);
                entitie.setHref(info.getAbsolutePath() + "/" + Long.toString(entitie.getId()));
                troubleTicketManagementFacade.edit(entitie);
                affectedRows = affectedRows + 1;
//                publisher.createNotification(entitie, new Date());
            }
//            affectedRows = troubleTicketManagementFacade.create(entities);
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
//            publisher.stateChangeNotification(entity, "TroubleTicket modified", new Date());
            // 200 OK + location
            response = Response.status(Response.Status.OK).entity(entity).build();

        } else {
            // 404 not found
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    /**
     *
     * For test purpose only
     *
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
     *
     * @param id
     * @return
     * @throws UnknownResourceException
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) throws UnknownResourceException {
        int previousRows = troubleTicketManagementFacade.count();
        TroubleTicket entity = troubleTicketManagementFacade.find(id);

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

    @GET
    @Path("proto")
    @Produces({"application/json"})
    public TroubleTicket proto() {
        TroubleTicket tt = new TroubleTicket();

        tt.setId(Long.getLong("42"));
        Date dt = new Date();
        //String dts = TMFDate.toString(dt);
        tt.setDescription("Some Description");
        tt.getCreationDate();

        tt.setCreationDate(TMFDate.toString(dt));
        tt.setStatus(Status.Acknowledged);

        tt.setSeverity(org.tmf.dsmapi.troubleTicket.model.Severity.High);
        tt.setType("Bills, charges or payment");

        // tt.setResolutionDate(dt); PG
        tt.setTargetResolutionDate(TMFDate.toString(dt));

        RelatedObject ro = new RelatedObject();
        ro.setInvolvement("involvment");
        ro.setReference("referenceobject");

        List<RelatedObject> relatedObjects = new ArrayList<RelatedObject>();
        relatedObjects.add(ro);
        relatedObjects.add(ro);
        tt.setRelatedObject(relatedObjects);

        RelatedParty rp = new RelatedParty();
        rp.setRole("role");
        rp.setId("any party identifer");
        //rp.setHjid("id"); //should be a string 
        rp.setHref("http//.../party/42");

        List<RelatedParty> relatedParties = new ArrayList<RelatedParty>();
        relatedParties.add(rp);
        relatedParties.add(rp);
        tt.setRelatedParty(relatedParties);

        Note note = new Note();
        note.setAuthor("author");
        note.setDate(dt);
        note.setText("text");
        List<Note> notes = new ArrayList<Note>();
        notes.add(note);
        notes.add(note);
        tt.setNote(notes);
        return tt;
    }

}
