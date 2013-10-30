package tmf.org.dsmapi.tt.service;

import tmf.org.dsmapi.commons.bean.Report;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.commons.utils.TMFDate;
import tmf.org.dsmapi.hub.service.EventFacade;
import tmf.org.dsmapi.hub.service.HubFacade;
import tmf.org.dsmapi.tt.Note;
import tmf.org.dsmapi.tt.RelatedObject;
import tmf.org.dsmapi.tt.RelatedParty;
import tmf.org.dsmapi.tt.Severity;
import tmf.org.dsmapi.tt.Status;
import tmf.org.dsmapi.tt.TroubleTicket;

/**
 *
 * @author maig7313
 */
@Stateless
@Path("admin")
public class AdminFacadeREST {

    @EJB
    TroubleTicketFacade ttManager;
    @EJB
    HubFacade hubManager;
    @EJB
    EventFacade eventManager;

    /**
     *
     * @param entities
     * @return
     */
    @POST
    @Path("troubleTicket")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response post(List<TroubleTicket> entities) {

        if (entities == null) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        int previousRows = ttManager.count();
        int affectedRows;

        // Try to persist entities
        try {
            affectedRows = ttManager.create(entities);
        } catch (BadUsageException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        Report stat = new Report(ttManager.count());
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
     */
    @DELETE
    @Path("troubleTicket")
    public Report deleteAllTT() {

        eventManager.removeAll();
        int previousRows = ttManager.count();
        ttManager.removeAll();
        int currentRows = ttManager.count();
        int affectedRows = previousRows - currentRows;        

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    /**
     *
     * @return
     */
    @DELETE
    @Path("hub")
    public Report deleteAllHub() {

        int previousRows = eventManager.count();
        hubManager.removeAll();
        int currentRows = eventManager.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    @DELETE
    @Path("event")
    public Report deleteAllEvent() {

        int previousRows = eventManager.count();
        eventManager.removeAll();
        int currentRows = eventManager.count();
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
    @Path("troubleTicket/{id}")
    public Report delete(@PathParam("id") String id) throws UnknownResourceException {

        int previousRows = ttManager.count();

        ttManager.remove(id);
        int affectedRows = 1;

        Report stat = new Report(ttManager.count());
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    /**
     *
     * @return
     */
    @GET
    @Path("troubleTicket/count")
    @Produces({"application/json"})
    public Report count() {
        return new Report(ttManager.count());
    }

    /**
     *
     */
    @DELETE
    @Path("troubleTicket/cache")
    public void clearCache() {
        ttManager.clearCache();
    }

    @PUT
    @Path("troubleTicket/wf/delay/{value}")
    public void patchDelay(@PathParam("value") long value) {
        ttManager.setDelay(value);
    }
    
}
