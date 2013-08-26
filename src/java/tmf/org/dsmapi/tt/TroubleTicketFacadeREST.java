/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt;
//changes22222 now look agan too much bbbbb cccc vvvvv last vvv mo

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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

        Response response;

        // 400 The requester cannot generate the id
        if (entity.getId() != null) {
            //Response response = Response.(entity).build();
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
            return response;
        }

        // 400 Check mandatory attributes
        if ((entity.getDescription() == null) || (entity.getSeverity() == null) || (entity.getType() == null)) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
            return response;
        }

        // Persist entity
        manager.create(entity);

        // 201 + location
        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
        String id = entity.getId();
        uriBuilder.path("{id}");
        return Response.created(uriBuilder.build(id)).
                entity(entity).
                build();

    }

    /*@PUT
     @Consumes({"application/json"})
     @Produces({"application/json"})
     public TroubleTicket edit(TroubleTicket entity) {
     super.edit(entity);
     return entity;
     }*/
    //Equivalent to PATCH also PUT with partial attributes is accepted
    //as replacement for PATCH but will get deprecated check behavior partial populated
    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public TroubleTicket postpatch(@PathParam("id") String id, TroubleTicket entity) {

        System.out.println("===PATCH is called ====");
        return entity;
    }

    @POST
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public TroubleTicket postPatch2(@PathParam("id") String id, TroubleTicket entity) {

        System.out.println("===PATCH is called ====");
        return entity;
    }

    @POST
    @Path("toto/{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public TroubleTicket postPatchtoto(@PathParam("id") String id, TroubleTicket entity) {

        System.out.println("===PATCH is called ====");
        return entity;
    }

    //X-HTTP-Method-Override on POST
    @PATCH
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public TroubleTicket patchRaw(@PathParam("id") String id, TroubleTicket entity) {

        System.out.println("===PATCH is called ====");
        return entity;
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

        String[] attributeTokens = null;
        List<String> tokenList;
        //Tokenize the attribute selector to find which attributes are requested
        if (as != null) {
            attributeTokens = as.split(",");
            tokenList = Arrays.asList(attributeTokens);
        } else {
            //adding all attributes
            tokenList = Arrays.asList();
            tokenList.add("all");
        }

        TroubleTicket responseTT = manager.find(id, tokenList);

        Response response = null;
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
        tt.setId("id");
        Date dt = new Date();
        String dts = toString(dt);
        tt.setDescription("Some Description");


        tt.setCreationDate(dts);
        tt.setStatus(Status.Acknowledged);
        tt.setSeverity(Severity.Medium);
        tt.setType("Bills, charges or payment");
        tt.setResolutionDate(dts);
        tt.setTargetResolutionDate(dts);

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
        note.setDate(dts);
        note.setText("text");
        Note notes[] = new Note[2];
        notes[0] = note;
        notes[1] = note;
        tt.setNotes(notes);
        return tt;

    }

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
