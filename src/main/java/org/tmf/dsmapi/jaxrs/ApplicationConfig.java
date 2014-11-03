package org.tmf.dsmapi.jaxrs;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("troubleTicketApi")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return getRestResourceClasses();
    }

    private Set<Class<?>> getRestResourceClasses() {
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        resources.add(org.tmf.dsmapi.commons.jaxrs.provider.BadUsageExceptionMapper.class);
        resources.add(org.tmf.dsmapi.commons.jaxrs.provider.JacksonConfigurator.class);
        resources.add(org.tmf.dsmapi.commons.jaxrs.provider.JsonMappingExceptionMapper.class);
        resources.add(org.tmf.dsmapi.commons.jaxrs.provider.UnknowResourceExceptionMapper.class);
        resources.add(org.tmf.dsmapi.jaxrs.resource.AdminResource.class);
        resources.add(org.tmf.dsmapi.jaxrs.resource.HubResource.class);
        resources.add(org.tmf.dsmapi.jaxrs.resource.TroubleTicketResource.class);
        // following code can be used to customize Jersey 1.x JSON provider:
        try {
            Class jacksonProvider = Class.forName("org.codehaus.jackson.jaxrs.JacksonJsonProvider");
            resources.add(jacksonProvider);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return resources;
    }
    
}
