/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.service.mapper;

import tmf.org.dsmapi.tt.service.JsonFault;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import tmf.org.dsmapi.commons.exceptions.TechnicalException;

@Provider
public class TechnicalExceptionMapper implements ExceptionMapper<TechnicalException> {
    @Override
    public Response toResponse(TechnicalException ex) {
        JsonFault error = new JsonFault(ex.getType().getInfo(),ex.getMessage());
        return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode()).entity(error).build();
    }
}
