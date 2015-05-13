package org.tmf.dsmapi.jaxrs.resource.hub;

import java.util.List;
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
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.jaxrs.model.Report;
import org.tmf.dsmapi.hub.model.Hub;
import org.tmf.dsmapi.troubleTicket.hub.service.TroubleTicketEventFacade;
import org.tmf.dsmapi.hub.service.HubFacade;

@Stateless
@Path("/troubleTicketManagement/v2/hub")
public class TroubleTicketHubResource {

    @EJB
    HubFacade hubFacade;
    @EJB
    TroubleTicketEventFacade eventFacade;

    public TroubleTicketHubResource() {
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response create(Hub entity) throws BadUsageException {
        entity.setId(null);
        hubFacade.create(entity);
        Response response = Response.ok(entity).build();
        return response;
    }

    @DELETE
   
    @Produces({"application/json"})
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
    @Produces({"application/json"})
    @Path("{id}")
    public Response remove(@PathParam("id") String id) throws UnknownResourceException {
        Hub hub = hubFacade.find(id);
        if (null == hub) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            hubFacade.remove(id);
            // 200 
            return Response.ok(hub).build();
        }
    }

    @GET
    @Produces({"application/json"})
    public List<Hub> findAll() {
        return hubFacade.findAll();
    }

}
