package org.tmf.dsmapi.jaxrs;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends ResourceConfig {
    
    public ApplicationConfig() {
        packages ("org.codehaus.jackson.jaxrs");
        
        
        
        register(org.tmf.dsmapi.commons.jaxrs.provider.BadUsageExceptionMapper.class);
        register(org.tmf.dsmapi.commons.jaxrs.provider.JacksonConfigurator.class);
        register(org.tmf.dsmapi.commons.jaxrs.provider.JsonMappingExceptionMapper.class);
        register(org.tmf.dsmapi.commons.jaxrs.provider.UnknowResourceExceptionMapper.class);
        register(org.tmf.dsmapi.jaxrs.resource.TroubleTicketResource.class);
        register(org.tmf.dsmapi.jaxrs.resource.admin.TroubleTicketAdminResource.class);
        register(org.tmf.dsmapi.jaxrs.resource.hub.TroubleTicketHubResource.class);
    }

  
    
}

 
