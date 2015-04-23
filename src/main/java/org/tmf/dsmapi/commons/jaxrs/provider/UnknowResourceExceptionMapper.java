/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.commons.jaxrs.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.jaxrs.model.JsonFault;

@Provider
public class UnknowResourceExceptionMapper implements ExceptionMapper<UnknownResourceException> {
    @Override
    public Response toResponse(UnknownResourceException ex) {
        JsonFault error = new JsonFault(ex.getType().getInfo(),ex.getMessage());
        return Response.status(404).entity(error).build();
    }
}
