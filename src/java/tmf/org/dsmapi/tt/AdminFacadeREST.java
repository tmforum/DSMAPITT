package tmf.org.dsmapi.tt;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import tmf.org.dsmapi.commons.exceptions.MandatoryFieldException;
import tmf.org.dsmapi.tt.model.TroubleTicket;

@Stateless
@Path("admin")
public class AdminFacadeREST {

    @EJB
    TroubleTicketFacade manager;

    @POST
    @Path("troubleTicket")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response post(List<TroubleTicket> entities) {

        // Try to persist entity
        try {
            manager.create(entities);
        } catch (MandatoryFieldException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        return Response.created(null).build();
    }

    @DELETE
    @Path("troubleTicket/{id}")
    public void delete(@PathParam("id") String id) {
        manager.remove(id);
    }
}
