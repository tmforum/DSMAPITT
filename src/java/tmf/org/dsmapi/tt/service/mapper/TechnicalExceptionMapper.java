/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.service.mapper;

import tmf.org.dsmapi.commons.bean.Fault;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import tmf.org.dsmapi.commons.exceptions.TechnicalException;

/**
 *
 * @author maig7313
 */
@Provider
public class TechnicalExceptionMapper implements ExceptionMapper<TechnicalException> {
    /**
     *
     * @param ex
     * @return
     */
    @Override
    public Response toResponse(TechnicalException ex) {
        Fault error = new Fault(ex.getType().getInfo(),ex.getMessage());
        return Response.status(Response.Status.SERVICE_UNAVAILABLE.getStatusCode()).entity(error).build();
    }
}
