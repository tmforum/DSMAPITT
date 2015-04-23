/*package org.tmf.dsmapi.jaxrs;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("api/troubleTicketManagement/v2")
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
        resources.add(org.tmf.dsmapi.jaxrs.resource.TroubleTicketResource.class);
        resources.add(org.tmf.dsmapi.jaxrs.resource.admin.TroubleTicketAdminResource.class);
        resources.add(org.tmf.dsmapi.jaxrs.resource.hub.TroubleTicketHubResource.class);
        return resources;
    }
    
}
*/

package org.tmf.dsmapi.jaxrs;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@javax.ws.rs.ApplicationPath("api/troubleTicketManagement/v2")
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

 
