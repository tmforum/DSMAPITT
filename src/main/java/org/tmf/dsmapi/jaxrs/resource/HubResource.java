package org.tmf.dsmapi.jaxrs.resource;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.node.ObjectNode;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.utils.Jackson;
import org.tmf.dsmapi.commons.utils.URIParser;
import org.tmf.dsmapi.hub.model.Event;
import org.tmf.dsmapi.hub.model.EventTypeEnum;
import org.tmf.dsmapi.hub.model.Hub;
import org.tmf.dsmapi.hub.service.EventFacade;
import org.tmf.dsmapi.hub.service.HubFacade;
import org.tmf.dsmapi.troubleTicket.model.TroubleTicket;

/**
 *
 * @author pierregauthier
 */
@Stateless
@Path("hub")
public class HubResource {

    @EJB
    HubFacade hubFacade;
    @EJB
    EventFacade eventFacade;

    public HubResource() {
    }

    @POST
    @Path("")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response create(Hub entity) throws BadUsageException {
        entity.setId(null);
        hubFacade.create(entity);
        Response response = Response.ok(entity).build();
        return response;
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") String id) throws UnknownResourceException {
        hubFacade.remove(id);
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Response findById(@PathParam("id") String id, @Context UriInfo info) throws UnknownResourceException {
        // fields to filter view
        Set<String> fieldSet = URIParser.getFieldsSelection(info.getQueryParameters());

        Hub entity = hubFacade.find(id);
        Response response;
        if (entity != null) {
            // 200
            if (fieldSet.isEmpty() || fieldSet.contains(URIParser.ALL_FIELDS)) {
                response = Response.ok(entity).build();
            } else {
                fieldSet.add(URIParser.ID_FIELD);
                ObjectNode node = Jackson.createNode(entity, fieldSet);
                response = Response.ok(node).build();
            }
        } else {
            // 404 not found
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    @GET
    @Produces({"application/json"})
    public List<Hub> findAll() {
        return hubFacade.findAll();
    }

    @POST
    @Path("listener")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public void publishEvent(Event event) {

        System.out.println("HubEvent =" + event);
        System.out.println("Event = " + event.getResource());
        System.out.println("Event type = " + event.getEventType());

    }


    @GET
    @Path("listener")
    @Produces({"application/json"})
    public Response findEvents(@Context UriInfo info) {

        // search criteria
        MultivaluedMap<String, String> parameters = URIParser.getParameters(info);
        // fields to filter view
        Set<String> fieldsSelection = URIParser.getFieldsSelection(parameters);

        Set<Event> resultList = findByCriteria(parameters);

        Response response;
        if (fieldsSelection.isEmpty() || fieldsSelection.contains(URIParser.ALL_FIELDS)) {
            response = Response.ok(resultList).build();
        } else {
            fieldsSelection.add(URIParser.ID_FIELD);
            List<ObjectNode> nodeList = Jackson.createNodes(resultList, fieldsSelection);
            response = Response.ok(nodeList).build();
        }
        return response;
    }

    // return Set of unique elements to avoid List with same elements in case of join
    private Set<Event> findByCriteria(MultivaluedMap<String, String> criteria) {
        List<Event> resultList = null;
        if (criteria != null && !criteria.isEmpty()) {
            resultList = eventFacade.findByCriteria(criteria, Event.class);
        } else {
            resultList = eventFacade.findAll();
        }
        if (resultList == null) {
            return new LinkedHashSet<Event>();
        } else {
            return new LinkedHashSet<Event>(resultList);
        }
    }

    @GET
    @Path("proto")
    @Produces({"application/json"})
    public Hub hubProto() {
        Hub hub = new Hub();
        hub.setCallback("callback");
        hub.setQuery("queryString");
        hub.setId("id");
        return hub;
    }

    @GET
    @Path("eventProto")
    @Produces({"application/json"})
    public Event eventProto() {
        Event event = new Event();
        event.setResource(proto());
        event.setEventType(EventTypeEnum.TicketCreateNotification);
        System.out.println("Event = " + event.getResource().toString());
        System.out.println("Event type = " + event.getEventType().getText());
        return event;
    }

    /**
     *
     * @return
     */
    public TroubleTicket proto() {
        TroubleTicket tt = new TroubleTicket();
        return tt;
    }
}
