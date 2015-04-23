/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.jaxrs.resource;

//import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.node.ObjectNode;
import org.glassfish.jersey.Severity;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.utils.Format;
import org.tmf.dsmapi.commons.utils.Jackson;
import org.tmf.dsmapi.commons.utils.PATCH;
import org.tmf.dsmapi.commons.utils.TMFDate;
import org.tmf.dsmapi.commons.utils.URIParser;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;
import org.tmf.dsmapi.troubleTicket.service.TroubleTicketFacade;
import org.tmf.dsmapi.troubleTicket.hub.service.TroubleTicketEventPublisherLocal;
import org.tmf.dsmapi.troubleTicket.model.Note;
import org.tmf.dsmapi.troubleTicket.model.RelatedObject;
import org.tmf.dsmapi.troubleTicket.model.RelatedParty;
import org.tmf.dsmapi.troubleTicket.model.Status;

@Stateless
@Path("troubleTicket")
public class TroubleTicketResource {

    @EJB
    TroubleTicketFacade troubleTicketManagementFacade;
    @EJB
    TroubleTicketEventPublisherLocal publisher;

    public TroubleTicketResource() {
    }

    /**
     * Test purpose only
     */
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response create(TroubleTicket entity) throws BadUsageException {
        troubleTicketManagementFacade.create(entity);
        publisher.createNotification(entity, "New TroubleTicket", new Date());
        // 201
        Response response = Response.status(Response.Status.CREATED).entity(entity).build();
        return response;
    }

    @GET
    @Produces({"application/json"})
    public Response find(@Context UriInfo info) throws BadUsageException {

        // search queryParameters
        MultivaluedMap<String, String> queryParameters = info.getQueryParameters();

        Map<String, List<String>> mutableMap = new HashMap();
        for (Map.Entry<String, List<String>> e : queryParameters.entrySet()) {
            mutableMap.put(e.getKey(), e.getValue());
        }

        // fields to filter view
        Set<String> fieldSet = URIParser.getFieldsSelection(mutableMap);

        Set<TroubleTicket> resultList = findByCriteria(mutableMap);

        Response response;
        if (fieldSet.isEmpty() || fieldSet.contains(URIParser.ALL_FIELDS)) {
            response = Response.ok(resultList).build();
        } else {
            fieldSet.add(URIParser.ID_FIELD);
            List<ObjectNode> nodeList = Jackson.createNodes(resultList, fieldSet);
            response = Response.ok(nodeList).build();
        }
        return response;
    }

    // return Set of unique elements to avoid List with same elements in case of join
    private Set<TroubleTicket> findByCriteria(Map<String, List<String>> criteria) throws BadUsageException {

        List<TroubleTicket> resultList = null;
        if (criteria != null && !criteria.isEmpty()) {
            resultList = troubleTicketManagementFacade.findByCriteria(criteria, TroubleTicket.class);
        } else {
            resultList = troubleTicketManagementFacade.findAll();
        }
        if (resultList == null) {
            return new LinkedHashSet<TroubleTicket>();
        } else {
            return new LinkedHashSet<TroubleTicket>(resultList);
        }
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Response get(@PathParam("id") long id, @Context UriInfo info) throws UnknownResourceException {

        
        
         // search queryParameters
        MultivaluedMap<String, String> queryParameters = info.getQueryParameters();

        Map<String, List<String>> mutableMap = new HashMap();
        for (Map.Entry<String, List<String>> e : queryParameters.entrySet()) {
            mutableMap.put(e.getKey(), e.getValue());
        }

        // fields to filter view
        Set<String> fieldSet = URIParser.getFieldsSelection(mutableMap);
        

//        // search parameter : id
//        MultivaluedMap<String, String> criteria = new MultivaluedMapImpl();
//        // Add the provided id to the map
//        criteria.add("id",id);
//        List<TroubleTicket>resultList = troubleTicketManagementFacade.findByCriteria(criteria, TroubleTicket.class);
        // fields to filter view

        TroubleTicket troubleTicketManagement = troubleTicketManagementFacade.find(id);
        Response response;
       
        // If the result list (list of bills) is not empty, it conains only 1 unique bill
        if (troubleTicketManagement != null) {
            // 200
            if (fieldSet.isEmpty() || fieldSet.contains(URIParser.ALL_FIELDS)) {
                response = Response.ok(troubleTicketManagement).build();
            } else {
                fieldSet.add(URIParser.ID_FIELD);
                ObjectNode node = Jackson.createNode(troubleTicketManagement, fieldSet);
                response = Response.ok(node).build();
            }
        } else {
            // 404 not found
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response update(@PathParam("id")  long id, TroubleTicket entity) throws UnknownResourceException {
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
    
    
    @PATCH
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response updatePartial( @PathParam("id") long id, TroubleTicket entity) throws UnknownResourceException ,BadUsageException{
        Response response = null;
        
        System.out.println("PATCH updatePartial is called ..." + id );
        TroubleTicket troubleTicketManagement = troubleTicketManagementFacade.find(id);
        if (troubleTicketManagement != null) {
            entity.setId(id);
             System.out.println("id  " + id );
            System.out.println("entity before partial edit " + entity );
            troubleTicketManagementFacade.partialEdit(entity);
            System.out.println("entity before partial edit 2" + entity );
            
            publisher.valueChangedNotification(entity, "TroubleTicket modified", new Date());
            // 201 OK + location
            response = Response.status(Response.Status.CREATED).entity(entity).build();

        } else {
            // 404 not found
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
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
        
        tt.setCreationDate(Format.toString(dt));
        tt.setStatus(Status.Acknowledged);
        
        tt.setSeverity(org.tmf.dsmapi.troubleTicket.model.Severity.High);
        tt.setType("Bills, charges or payment");
        
       // tt.setResolutionDate(dt); PG
        tt.setTargetResolutionDate(Format.toString(dt));
       
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
