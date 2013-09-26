package tmf.org.dsmapi.tt.service;

import java.util.ArrayList;
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
import tmf.org.dsmapi.tt.service.TroubleTicketFacade;
import tmf.org.dsmapi.tt.Note;
import tmf.org.dsmapi.tt.RelatedObject;
import tmf.org.dsmapi.tt.RelatedParty;
import tmf.org.dsmapi.tt.Severity;
import tmf.org.dsmapi.tt.Status;
import tmf.org.dsmapi.tt.TroubleTicket;

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

        int affectedRows = manager.removeAll();

        Report stat = new Report(manager.count());
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(affectedRows);

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
    
    @DELETE
    @Path("troubleTicket/cache")
    public void clearCache() {
        manager.invalidCache();
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
        
        List<RelatedObject> relatedObjects = new ArrayList<RelatedObject> ();
        relatedObjects.add(ro);
        relatedObjects.add(ro);
        tt.setRelatedObjects(relatedObjects);

        RelatedParty rp = new RelatedParty();
        rp.setRole("role");
        rp.setReference("reference party");

        List<RelatedParty> relatedParties = new ArrayList<RelatedParty> ();
        relatedParties.add(rp);
        relatedParties.add(rp);        
        tt.setRelatedParties(relatedParties);

        Note note = new Note();
        note.setAuthor("author");
        note.setDate(dts);
        note.setText("text");
        List<Note> notes = new ArrayList<Note> ();
        notes.add(note);
        notes.add(note);
        tt.setNotes(notes);
        return tt;

    }
}
