/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.jaxrs.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.TechnicalException;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.utils.Jackson;
import org.tmf.dsmapi.commons.utils.PATCH;
import org.tmf.dsmapi.commons.utils.TMFDate;
import org.tmf.dsmapi.commons.utils.URIParser;
import org.tmf.dsmapi.hub.service.EventPublisherLocal;
import org.tmf.dsmapi.troubleTicket.model.Note;
import org.tmf.dsmapi.troubleTicket.model.RelatedObject;
import org.tmf.dsmapi.troubleTicket.model.RelatedParty;
import org.tmf.dsmapi.troubleTicket.model.Severity;
import org.tmf.dsmapi.troubleTicket.model.Status;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicketField;
import org.tmf.dsmapi.troubleTicket.service.TroubleTicketFacade;

/**
 *
 * @author pierregauthier
 */
@Stateless
@Path("troubleTicket")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TroubleTicketResource {

    @EJB
    TroubleTicketFacade troubleTicketFacade;
    @EJB
    EventPublisherLocal publisher;

    public TroubleTicketResource() {
    }

    @GET
    @Produces({"application/json"})
    public Response findByCriteriaWithFields(@Context UriInfo info) throws BadUsageException {

        // search queryParameters
        MultivaluedMap<String, String> queryParameters = info.getQueryParameters();
        // fields to filter view
        Set<String> fieldSet = URIParser.getFieldsSelection(queryParameters);

        Set<TroubleTicket> resultList = findByCriteria(queryParameters);

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
    private Set<TroubleTicket> findByCriteria(MultivaluedMap<String, String> criteria) throws BadUsageException {

        List<TroubleTicket> resultList = null;
        if (criteria != null && !criteria.isEmpty()) {
            resultList = troubleTicketFacade.findByCriteria(criteria, TroubleTicket.class);
        } else {
            resultList = troubleTicketFacade.findAll();
        }
        if (resultList == null) {
            return new LinkedHashSet<TroubleTicket>();
        } else {
            return new LinkedHashSet<TroubleTicket>(resultList);
        }
    }

    /**
     *
     * @param id
     * @param info
     * @return
     * @throws UnknownResourceException
     */
    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Response findById(@PathParam("id") String id, @Context UriInfo info) throws UnknownResourceException {

        // fields to filter view
        Set<String> fieldSet = URIParser.getFieldsSelection(info.getQueryParameters());

        TroubleTicket troubleTicket = troubleTicketFacade.find(id);
        Response response;
        if (troubleTicket != null) {
            // 200
            if (fieldSet.isEmpty() || fieldSet.contains(URIParser.ALL_FIELDS)) {
                response = Response.ok(troubleTicket).build();
            } else {
                fieldSet.add(URIParser.ID_FIELD);
                ObjectNode node = Jackson.createNode(troubleTicket, fieldSet);
                response = Response.ok(node).build();
            }
        } else {
            // 404 not found
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    /**
     *
     * @param entity
     * @return
     * @throws BadUsageException
     * @throws TechnicalException
     */
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response create(TroubleTicket entity) throws BadUsageException, TechnicalException {

        entity.setStatus(Status.Submitted);
        entity.setStatusChangeDate(TMFDate.toString(new Date()));
        troubleTicketFacade.create(entity);
        publisher.createNotification(entity, "New TroubleTicket", new Date());
        // 201
        Response response = Response.status(Response.Status.CREATED).entity(entity).build();
        return response;

    }

    /**
     *
     * @param id
     * @param entity
     * @return
     * @throws UnknownResourceException
     */
    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response put(@PathParam("id") String id, TroubleTicket entity) throws UnknownResourceException {

        Response response = null;
        TroubleTicket troubleTicket = troubleTicketFacade.find(id);
        if (troubleTicket != null) {
            entity.setId(id);
            troubleTicketFacade.edit(entity);
            publisher.changedNotification(entity, "TroubleTicket Modified", new Date());
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
     * @param id
     * @param jsonNode
     * @return
     * @throws BadUsageException
     * @throws UnknownResourceException
     */
    @PATCH
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response patch(@PathParam("id") String id, JsonNode jsonNode) throws BadUsageException, UnknownResourceException {

        Set<TroubleTicketField> fields = new HashSet<TroubleTicketField>();

        // Iterate over first level to set received tokens
        Iterator<String> it = jsonNode.getFieldNames();
        while (it.hasNext()) {
            fields.add(TroubleTicketField.fromString(it.next()));
        }

        ObjectMapper mapper = new ObjectMapper();
        TroubleTicket partialTT;
        try {
            partialTT = mapper.readValue(jsonNode, TroubleTicket.class);
        } catch (Exception ex) {
            Logger.getLogger(TroubleTicketResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new TechnicalException();
        }

        // id is in URL
        partialTT.setId(id);

        // Status is updated when correlationId==nul ( admin or demo purpose only)
        TroubleTicket fullTT = troubleTicketFacade.updateAttributes(partialTT, fields);

        // 201
        Response response = Response.status(Response.Status.CREATED).entity(fullTT).build();
        return response;
        
    }

    /**
     *
     * @return
     */
    @GET
    @Path("mock")
    @Produces({"application/json"})
    public TroubleTicket mock() {
        TroubleTicket tt = new TroubleTicket();
        tt.setId("id");
        Date dt = new Date();
        String dts = TMFDate.toString(dt);
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

        List<RelatedObject> relatedObjects = new ArrayList<RelatedObject>();
        relatedObjects.add(ro);
        relatedObjects.add(ro);
        tt.setRelatedObjects(relatedObjects);

        RelatedParty rp = new RelatedParty();
        rp.setRole("role");
        rp.setReference("reference party");

        List<RelatedParty> relatedParties = new ArrayList<RelatedParty>();
        relatedParties.add(rp);
        relatedParties.add(rp);
        tt.setRelatedParties(relatedParties);

        Note note = new Note();
        note.setAuthor("author");
        note.setDate(dts);
        note.setText("text");
        List<Note> notes = new ArrayList<Note>();
        notes.add(note);
        notes.add(note);
        tt.setNotes(notes);
        return tt;

    }
}
