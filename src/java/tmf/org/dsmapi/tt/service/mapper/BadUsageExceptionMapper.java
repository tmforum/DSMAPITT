/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.tt.service.mapper;

import tmf.org.dsmapi.commons.bean.Fault;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;

/**
 *
 * @author maig7313
 */
@Provider
public class BadUsageExceptionMapper implements ExceptionMapper<BadUsageException> {
    /**
     *
     * @param ex
     * @return
     */
    @Override
    public Response toResponse(BadUsageException ex) {
        Fault error = new Fault(ex.getType().getInfo(),ex.getMessage());
        return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity(error).build();
    }
}
