package tmf.org.dsmapi.tt.jaxrs.mapping;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import tmf.org.dsmapi.tt.model.TroubleTicket;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

@Provider
@Produces({"application/json"})
public class TroubleTicketWriter implements MessageBodyWriter<TroubleTicket> {

    @Context
    UriInfo info;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return TroubleTicket.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(TroubleTicket t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(TroubleTicket tt, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        // search queryParameters
        MultivaluedMap<String, String> queryParameters = info.getQueryParameters();
        // fields to filter view
        Set<String> fieldSet = FacadeRestUtil.getFieldSet(queryParameters);

        if (fieldSet.isEmpty() || fieldSet.contains(FacadeRestUtil.ALL_FIELDS)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(entityStream, tt);
        } else {
            fieldSet.add(FacadeRestUtil.ID_FIELD);
            ObjectNode root = FacadeRestUtil.createNodeViewWithFields(tt, fieldSet);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(entityStream, root);
        }
    }
}
