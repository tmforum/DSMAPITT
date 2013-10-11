/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.service;
//changes22222 now look agan too much bbbbb cccc vvvvv last vvv mo

import tmf.org.dsmapi.commons.jaxrs.PATCH;
import tmf.org.dsmapi.tt.TroubleTicket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.TechnicalException;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.commons.utils.Format;
import tmf.org.dsmapi.hub.service.PublisherLocal;
import tmf.org.dsmapi.tt.Status;
import tmf.org.dsmapi.tt.TroubleTicketField;
import tmf.org.dsmapi.tt.workflow.WorkFlow;

/**
 *
 * @author pierregauthier
 */
@Stateless
@Path("troubleTicket")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TroubleTicketFacadeREST {

    @Context
    UriInfo uriInfo;
    @EJB
    TroubleTicketFacade manager;
    @EJB
    PublisherLocal publisher;
    @EJB
    WorkFlow workflow;

    /**
     *
     */
    public TroubleTicketFacadeREST() {
    }

    /*
     * RESOURCE
     * troubleTicket
     */
    /**
     *
     * @param info
     * @return
     */
    @GET
    @Produces({"application/json"})
    public Response list(@Context UriInfo info) {

        Response response = null;
        // search queryParameters
        MultivaluedMap<String, String> queryParameters = info.getQueryParameters();
        // fields to filter view
        Set<String> fieldSet = RestUtils.getFieldSet(queryParameters);

        List<TroubleTicket> resultList = manager.find(queryParameters);

        if (fieldSet.isEmpty() || fieldSet.contains(RestUtils.ALL_FIELDS)) {

            response = Response.ok(resultList).build();

        } else {

            fieldSet.add(RestUtils.ID_FIELD);
            List<ObjectNode> nodeList = new ArrayList<ObjectNode>();
            ObjectNode node;
            for (TroubleTicket tt : resultList) {
                node = RestUtils.createNodeViewWithFields(tt, fieldSet);
                nodeList.add(node);
            }
            response = Response.ok(nodeList).build();

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
    public Response post(TroubleTicket entity) throws BadUsageException, TechnicalException {

        entity.setStatus(Status.Submitted);
        entity.setStatusChangeDate(Format.toString(new Date()));
        manager.create(entity);

        workflow.start(entity);

        // 201 OK + location
        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
        String id = entity.getId();
        uriBuilder.path("{id}");
        return Response.created(uriBuilder.build(id)).
                entity(entity).
                build();

    }

    /*
     * RESOURCE
     * troubleTicket/{id}
     */
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

        // Try to updateAttributes
        entity.setId(id);
        entity = manager.edit(entity);

        // 201 OK + location
        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
        uriBuilder.path("{id}");
        return Response.created(uriBuilder.build(id)).
                entity(entity).
                build();
    }

    //X-HTTP-Method-Override on POST
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
            Logger.getLogger(TroubleTicketFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            throw new TechnicalException();
        }

        // id is in URL
        partialTT.setId(id);

        // Status is updated when correlationId==nul ( admin or demo purpose only)
        TroubleTicket fullTT = manager.updateAttributes(partialTT, fields);

        // When correlationId!=null
        if ((partialTT.getCorrelationId() != null) && !partialTT.getCorrelationId().isEmpty()) {
            // should use correlationId to wakeUp, but as there is only one case in demo... for Pending..
            workflow.wakeUp(fullTT);
        }

        // 201 OK + location
        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
        id = fullTT.getId();
        uriBuilder.path("{id}");
        return Response.created(uriBuilder.build(id)).
                entity(fullTT).
                build();
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
    public Response get(@PathParam("id") String id, @Context UriInfo info) throws UnknownResourceException {

        Response response = null;
        TroubleTicket tt = manager.find(id);

        MultivaluedMap<String, String> queryParameters = info.getQueryParameters();
        Set<String> fieldSet = RestUtils.getFieldSet(queryParameters);

        if (fieldSet.isEmpty() || fieldSet.contains(RestUtils.ALL_FIELDS)) {
            response = Response.ok(tt).build();
        } else {
            fieldSet.add(RestUtils.ID_FIELD);
            ObjectNode root = RestUtils.createNodeViewWithFields(tt, fieldSet);
            response = Response.ok(root).build();
        }

        return response;
    }
}
