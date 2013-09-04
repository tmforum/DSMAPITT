/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * Jersey REST client generated for REST resource:HubFacadeREST
 * [tmf.org.dsmapi.hub.hub]<br>
 * USAGE:
 * <pre>
 *        PostEventClient client = new PostEventClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author pierregauthier
 */
public class PostEventClient {

    private WebResource webResource;
    private Client client;

    public PostEventClient(String callbackURL) {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(callbackURL).path("");
    }

    public void publishEvent(Object requestEntity) throws UniformInterfaceException {
        webResource.path("").type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(requestEntity);
    }

    public void close() {
        client.destroy();
    }
}
