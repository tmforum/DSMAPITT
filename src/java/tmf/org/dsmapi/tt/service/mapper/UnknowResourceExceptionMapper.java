/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.service.mapper;

import tmf.org.dsmapi.tt.service.JsonFault;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;

@Provider
public class UnknowResourceExceptionMapper implements ExceptionMapper<UnknownResourceException> {
    @Override
    public Response toResponse(UnknownResourceException ex) {
        JsonFault error = new JsonFault(ex.getType().getInfo(),ex.getMessage());
        return Response.status(404).entity(error).build();
    }
}
