package tmf.org.dsmapi.tt.jaxrs;

import java.util.Date;
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
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.commons.utils.Format;
import tmf.org.dsmapi.tt.facade.TroubleTicketFacade;
import tmf.org.dsmapi.tt.jaxrs.model.Report;
import tmf.org.dsmapi.tt.model.Note;
import tmf.org.dsmapi.tt.model.RelatedObject;
import tmf.org.dsmapi.tt.model.RelatedParty;
import tmf.org.dsmapi.tt.model.Severity;
import tmf.org.dsmapi.tt.model.Status;
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
        
        if (entities==null) return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build(); 

        int previousRows = manager.count();
        int affectedRows;

        // Try to persist entities
        try {
            affectedRows = manager.create(entities);
        } catch (BadUsageException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        Report stat = new Report(manager.count());
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        // 201 OK
        return Response.created(null).
                entity(stat).
                build();
    }

    @DELETE
    @Path("troubleTicket")
    public Report deleteAll() {

        int previousRows = manager.count();
        int affectedRows = manager.removeAll();

        Report stat = new Report(manager.count());
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    @DELETE
    @Path("troubleTicket/{id}")
    public Report delete(@PathParam("id") String id) throws UnknownResourceException {

        int previousRows = manager.count();

        manager.remove(id);
        int affectedRows = 1;

        Report stat = new Report(manager.count());
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    @GET
    @Path("troubleTicket/count")
    @Produces({"application/json"})
    public Report count() {
        return new Report(manager.count());
    }

    @GET
    @Path("troubleTicket/mock")
    @Produces({"application/json"})
    public TroubleTicket proto() {
        TroubleTicket tt = new TroubleTicket();
        tt.setId("id");
        Date dt = new Date();
        String dts = Format.toString(dt);
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
}
