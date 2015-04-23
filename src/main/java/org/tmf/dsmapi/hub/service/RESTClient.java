package org.tmf.dsmapi.hub.service;

//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.UniformInterfaceException;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.glassfish.jersey.client.ClientConfig;
import java.util.HashMap;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.node.ObjectNode;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;

@Stateless
public class RESTClient {

    private static Client jaxrsClient;
//    private HashMap<String, WebResource> webResources = new HashMap<String, WebResource>();
    private HashMap<String, WebTarget> webResources = new HashMap<String, WebTarget>();

    public void publishEvent(String callbackURL, Object requestEntity) {
        System.out.println("publishEvent " + requestEntity);
        WebTarget webResource = getWebResource(callbackURL);
        Entity entity = Entity.entity(requestEntity, MediaType.APPLICATION_JSON);
        try {
        webResource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(entity);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        //webResource.path("").type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(requestEntity);
    }
    
    public void publishEvent(String callbackURL, ObjectNode node) {
        System.out.println("publishEvent " + node);
        WebTarget webResource = getWebResource(callbackURL);
        Entity entity = Entity.entity(node, MediaType.APPLICATION_JSON);
        try {
        webResource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(entity);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        //webResource.path("").type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(requestEntity);
    }    

   private Client getJaxrsClient() {
        if (jaxrsClient == null) {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(SerializationConfig.Feature.INDENT_OUTPUT, true).configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
            JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
            jacksonProvider.setMapper(mapper);
            ClientConfig config = new ClientConfig();
            ClientConfig register = config.register(jacksonProvider);
            
            
           
           
            
            jaxrsClient = ClientBuilder.newClient(config);
            jaxrsClient.register(JacksonFeature.class);
            jaxrsClient.property(ClientProperties.CONNECT_TIMEOUT, 3000);
            jaxrsClient.property(ClientProperties.READ_TIMEOUT, 3000);
        }
        return jaxrsClient;
    }

    // In memory caching, webResources and client are thread safe see jersey doc
    private WebTarget getWebResource(String endpointURL) {
        if (!webResources.containsKey(endpointURL)) {
            WebTarget webResource = getJaxrsClient().target(endpointURL).path("");
            webResources.put(endpointURL, webResource);
        }
        return webResources.get(endpointURL);
    }
}
