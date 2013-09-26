/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import tmf.org.dsmapi.commons.utils.Format;
import tmf.org.dsmapi.hub.Hub;
import tmf.org.dsmapi.hub.HubEvent;
import tmf.org.dsmapi.hub.TroubleTicketEventTypeEnum;
import tmf.org.dsmapi.tt.Note;
import tmf.org.dsmapi.tt.RelatedObject;
import tmf.org.dsmapi.tt.RelatedParty;
import tmf.org.dsmapi.tt.Severity;
import tmf.org.dsmapi.tt.Status;
import tmf.org.dsmapi.tt.TroubleTicket;

/**
 *
 * @author pierregauthier
 */
@Stateless
@Path("tmf.org.dsmapi.hub.hub")
public class HubFacadeREST extends AbstractFacade<Hub> {
    @PersistenceContext(unitName = "DSTroubleTicketPU")
    private EntityManager em;

    public HubFacadeREST() {
        super(Hub.class);
    }

    @POST
    @Override
    @Consumes({"application/json"})
    public void create(Hub entity) {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes({"application/xml", "application/json"})
    public void edit(Hub entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") String id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({ "application/json"})
    public Hub find(@PathParam("id") String id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/json"})
    public List<Hub> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/json"})
    public List<Hub> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @POST
    @Path("listener")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public void publishEvent(HubEvent event) {

        System.out.println("HubEvent =" + event );
        System.out.println("Event = " + event.getEvent().toString());
        System.out.println("Event type = " + event.getEventType().getText());
        
    }
    
    @GET
    @Path("proto")
    @Produces({ "application/json"})
    public Hub hubProto() {
        Hub hub = new Hub();
        hub.setCallback("callback");
        hub.setQuery("queryString");
        hub.setId("id");
        return hub;
    }
    
    @GET
    @Path("eventProto")
    @Produces({ "application/json"})
    public HubEvent eventProto() {
        HubEvent event = new HubEvent();
        event.setEvent(proto());
        event.setEventType(TroubleTicketEventTypeEnum.TicketCreateNotification);
        System.out.println("Event = " + event.getEvent().toString());
        System.out.println("Event type = " + event.getEventType().getText());
        return event;
    }
    
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
