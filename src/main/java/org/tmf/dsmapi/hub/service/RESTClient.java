package org.tmf.dsmapi.hub.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.util.HashMap;
import javax.ejb.Stateless;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.node.ObjectNode;

@Stateless
public class RESTClient {

    private static Client jaxrsClient;
    private HashMap<String, WebResource> webResources = new HashMap<String, WebResource>();

    public void publishEvent(String callbackURL, Object requestEntity) throws UniformInterfaceException {
        System.out.println("publishEvent " + requestEntity);
        WebResource webResource = getWebResource(callbackURL);
        webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(requestEntity);
        //webResource.path("").type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(requestEntity);
    }
    
    public void publishEvent(String callbackURL, ObjectNode node) throws UniformInterfaceException {
        System.out.println("publishEvent " + node);
        WebResource webResource = getWebResource(callbackURL);
        webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(node);
        //webResource.path("").type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(requestEntity);
    }    

    private Client getJaxrsClient() {
        if (jaxrsClient == null) {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(SerializationConfig.Feature.INDENT_OUTPUT, true).configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
            JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
            jacksonProvider.setMapper(mapper);
            ClientConfig config = new DefaultClientConfig();
            config.getSingletons().add(jacksonProvider);
            jaxrsClient = Client.create(config);
            jaxrsClient.setConnectTimeout(new Integer("3000"));
            jaxrsClient.setReadTimeout(new Integer("3000"));
        }
        return jaxrsClient;
    }

    // In memory caching, webResources and client are thread safe see jersey doc
    private WebResource getWebResource(String endpointURL) {
        if (!webResources.containsKey(endpointURL)) {
            WebResource webResource = getJaxrsClient().resource(endpointURL).path("");
            webResources.put(endpointURL, webResource);
        }
        return webResources.get(endpointURL);
    }
}
