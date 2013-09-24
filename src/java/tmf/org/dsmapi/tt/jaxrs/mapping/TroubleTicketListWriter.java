package tmf.org.dsmapi.tt.jaxrs.mapping;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import tmf.org.dsmapi.tt.model.TroubleTicket;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

@Provider
@Produces({"application/json"})
public class TroubleTicketListWriter implements
        MessageBodyWriter<List<TroubleTicket>> {

    @Context
    UriInfo info;

    @Override
    public long getSize(List<TroubleTicket> arg0, Class<?> arg1,
            Type arg2, Annotation[] arg3, MediaType arg4) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] arg2, MediaType arg3) {

        // Ensure that we're handling only List<TroubleTicket> objects.
        boolean isWritable;
        if (List.class.isAssignableFrom(type)
                && genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] actualTypeArgs = (parameterizedType.getActualTypeArguments());
            isWritable = (actualTypeArgs.length == 1 && actualTypeArgs[0]
                    .equals(TroubleTicket.class));
        } else {
            isWritable = false;
        }

        return isWritable;
    }

    @Override
    public void writeTo(List<TroubleTicket> ttList,
            Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4,
            MultivaluedMap<String, Object> arg5, OutputStream os)
            throws IOException {

        // search criteria
        MultivaluedMap<String, String> criteria = info.getQueryParameters();
        // fields to filter view
        Set<String> fieldSet = FacadeRestUtil.getFieldSet(criteria);

        if (fieldSet.isEmpty() || fieldSet.contains(FacadeRestUtil.ALL_FIELDS)) {
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(os, ttList);
            
        } else {
            
            fieldSet.add(FacadeRestUtil.ID_FIELD);
            List<ObjectNode> nodeList = new ArrayList<ObjectNode>();
            ObjectNode node;
            for (TroubleTicket tt : ttList) {
                node = FacadeRestUtil.createNodeViewWithFields(tt, fieldSet);
                nodeList.add(node);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(os, nodeList);
            
        }

    }
}
