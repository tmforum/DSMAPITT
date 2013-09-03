/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt;
//changes22222 now look agan too much bbbbb cccc vvvvv last vvv mo

import tmf.org.dsmapi.tt.model.TroubleTicketAttributesEnum;
import tmf.org.dsmapi.tt.model.RelatedObject;
import tmf.org.dsmapi.tt.model.Severity;
import tmf.org.dsmapi.tt.model.RelatedParty;
import tmf.org.dsmapi.tt.model.TroubleTicket;
import tmf.org.dsmapi.tt.model.Note;
import tmf.org.dsmapi.tt.model.Status;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.MandatoryFieldException;
import tmf.org.dsmapi.commons.exceptions.StatusException;
import tmf.org.dsmapi.commons.utils.Format;

/**
 *
 * @author pierregauthier
 */
@Stateless
@Path("tmf.org.dsmapi.tt.troubleticket")
public class TroubleTicketFacadeREST {

    @Context
    UriInfo uriInfo;
    @EJB
    TroubleTicketFacade manager;

    public TroubleTicketFacadeREST() {
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response create(TroubleTicket entity) {

        // 400 
        if (entity.getId() != null) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        try {
            // Try to persist entity
            manager.create(entity);
        } catch (MandatoryFieldException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        // 201 OK + location
        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
        String id = entity.getId();
        uriBuilder.path("{id}");
        return Response.created(uriBuilder.build(id)).
                entity(entity).
                build();

    }

    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response edit(@PathParam("id") String id, TroubleTicket entity) {

        // 400 resource id and entity id must be the same
        if (!entity.getId().equalsIgnoreCase(id)) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        // 400 
        if (!Validator.hasMandatoryFields(entity)) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        // Try to merge        
        manager.edit(entity);

        // 201 OK + location
        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
        uriBuilder.path("{id}");
        return Response.created(uriBuilder.build(id)).
                entity(entity).
                build();
    }

    //X-HTTP-Method-Override on POST
    @PATCH
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response patch(@PathParam("id") String id, TroubleTicket partialTT) {

        // 400 resource id and entity id must be the same
        if (!partialTT.getId().equalsIgnoreCase(id)) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        TroubleTicket fullTT;
        
        try {
            fullTT = manager.partialUpdate(partialTT);
        } catch (BadUsageException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        } catch (StatusException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        // if troubleTicket exists
        if (fullTT == null) {
            // 404 not found
            return Response.status(404).build();

        } else {
            // 201 OK + location
            UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
            id = fullTT.getId();
            uriBuilder.path("{id}");
            return Response.created(uriBuilder.build(id)).
                    entity(fullTT).
                    build();
        }

    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") String id) {
        manager.remove(id);
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Response find(@PathParam("id") String id) {

        TroubleTicket tt = manager.find(id);

        Response response = null;

        // if troubleTicket exists
        if (tt != null) {
            // 200
            response = Response.ok(tt).build();
        } else {
            // 404 not found
            response = Response.status(404).build();
        }

        return response;
    }

    @GET
    @Path("{id}/{attributes}")
    @Produces({"application/json"})
    public Response findWithAttributes(@PathParam("id") String id, @PathParam("attributes") String as) {

        Set<TroubleTicketAttributesEnum> tokenList = new HashSet<TroubleTicketAttributesEnum>();

        // Convert attributes parameter to a set of TroubleTicketAttributesEnum
        if (as != null) {
            String[] tokenArray = as.split(",");
            for (int i = 0; i < tokenArray.length; i++) {
                tokenList.add(TroubleTicketAttributesEnum.fromString(tokenArray[i]));
            }
        } else {
            // ALL
            tokenList.add(TroubleTicketAttributesEnum.ALL);
        }

        TroubleTicket responseTT = manager.find(id, tokenList);

        Response response;
        // if troubleTicket exists
        if (responseTT != null) {
            // 200
            response = Response.ok(responseTT).build();
        } else {
            // 404 not found
            response = Response.status(404).build();
        }

        return response;
    }

    @GET
    @Produces({"application/json"})
    public List<TroubleTicket> findAll() {
        return manager.findAll();
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(manager.count());
    }

    @GET
    @Path("proto")
    @Produces({"application/json"})
    public TroubleTicket proto() {
        TroubleTicket tt = new TroubleTicket();
        Date dt = new Date();
        String dts = Format.toString(dt);
        tt.setDescription("Some Description");


        tt.setCreationDate(dts);
        tt.setStatus(Status.Acknowledged);
        tt.setSeverity(Severity.Medium);
        tt.setType("Bills, charges or payment");

        RelatedObject ro = new RelatedObject();
        ro.setInvolvement("involvment");
        ro.setReference("referenceobject");

        RelatedObject relatedObjects[] = new RelatedObject[2];
        relatedObjects[0] = ro;
        relatedObjects[1] = ro;
        tt.setRelatedObjects(relatedObjects);

        RelatedParty rp = new RelatedParty();
        rp.setRole("role");
        rp.setReference("reference party");

        RelatedParty relatedParties[] = new RelatedParty[2];
        relatedParties[0] = rp;
        relatedParties[1] = rp;
        tt.setRelatedParties(relatedParties);

        Note note = new Note();
        note.setAuthor("author");
        note.setDate("date");
        note.setText("text");
        Note notes[] = new Note[2];
        notes[0] = note;
        notes[1] = note;
        tt.setNotes(notes);
        return tt;

    }

    public static Date parse(String input) throws java.text.ParseException {

        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        //this is zero time so we need to add that TZ indicator for 
        if (input.endsWith("Z")) {
            input = input.substring(0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring(0, input.length() - inset);
            String s1 = input.substring(input.length() - inset, input.length());

            input = s0 + "GMT" + s1;
        }

        return df.parse(input);

    }
}
