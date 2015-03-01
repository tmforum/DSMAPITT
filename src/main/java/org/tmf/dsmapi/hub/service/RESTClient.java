package org.tmf.dsmapi.hub.service;

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
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

@Stateless
public class RESTClient {

    private static Client jaxrsClient;
    private final HashMap<String, WebTarget> webTarget = new HashMap<String, WebTarget>();

    public void publishEvent(String callbackURL, Object requestEntity) {
        System.out.println("publishEvent " + requestEntity);
        WebTarget webResource = getWebResource(callbackURL);
        Entity entity = Entity.entity(requestEntity, MediaType.APPLICATION_JSON);
        webResource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(entity);
    }
    
    public void publishEvent(String callbackURL, ObjectNode node) {
        System.out.println("publishEvent " + node);
        WebTarget webResource = getWebResource(callbackURL);
        Entity entity = Entity.entity(node, MediaType.APPLICATION_JSON);
        webResource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(entity);
    }    

    private Client getJaxrsClient() {
        if (jaxrsClient == null) {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(SerializationConfig.Feature.INDENT_OUTPUT, true).configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
            JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
            jacksonProvider.setMapper(mapper);
            ClientConfig config = new ClientConfig();
            config.register(jacksonProvider);
            jaxrsClient = ClientBuilder.newClient(config);
            jaxrsClient.property(ClientProperties.CONNECT_TIMEOUT, 3000);
            jaxrsClient.property(ClientProperties.READ_TIMEOUT, 3000);
        }
        return jaxrsClient;
    }

    // In memory caching, webTarget and client are thread safe see jersey doc
    private WebTarget getWebResource(String endpointURL) {
        if (!webTarget.containsKey(endpointURL)) {
            WebTarget webResource = getJaxrsClient().target(endpointURL).path("");
            webTarget.put(endpointURL, webResource);
        }
        return webTarget.get(endpointURL);
    }
}