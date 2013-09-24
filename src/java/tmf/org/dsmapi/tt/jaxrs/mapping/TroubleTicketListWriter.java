package tmf.org.dsmapi.tt.jaxrs.mapping;

import tmf.org.dsmapi.commons.utils.ReservedKeyword;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import tmf.org.dsmapi.tt.model.TroubleTicket;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import tmf.org.dsmapi.commons.utils.ReservedKeyword;
import tmf.org.dsmapi.tt.model.TroubleTicketField;

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

        MultivaluedMap<String, String> map = info.getQueryParameters();

        List<String> fields = null;
        if (map.containsKey(ReservedKeyword.QUERY_KEY_FIELD.getText())) {
            fields = map.get(ReservedKeyword.QUERY_KEY_FIELD.getText());
        }
        if (map.containsKey(ReservedKeyword.QUERY_KEY_FIELD_2.getText())) {
            fields = map.get(ReservedKeyword.QUERY_KEY_FIELD_2.getText());
        }

        if (fields != null) {

            Set<TroubleTicketField> template = FieldSelection.getFields(fields);

            List<ObjectNode> list = new ArrayList<ObjectNode>();
            ObjectNode item;

            for (TroubleTicket tt : ttList) {
                item = TroubleTicketJsonMaker.getView(tt, template);
                list.add(item);
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(os, list);

        } else {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(os, ttList);
        }
    }
}
