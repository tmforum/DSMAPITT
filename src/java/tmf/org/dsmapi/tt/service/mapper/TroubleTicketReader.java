package tmf.org.dsmapi.tt.service.mapper;

import tmf.org.dsmapi.tt.TroubleTicketField;
import tmf.org.dsmapi.tt.TroubleTicket;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 */
@Provider
@Consumes({"application/json"})
public class TroubleTicketReader implements MessageBodyReader<TroubleTicket> {

    /**
     *
     * @param type
     * @param genericType
     * @param annotations
     * @param mediaType
     * @return
     */
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return TroubleTicket.class.isAssignableFrom(type);
    }

    /**
     *
     * @param type
     * @param type1
     * @param antns
     * @param mediaType
     * @param httpHeaders
     * @param entityStream
     * @return
     * @throws IOException
     * @throws WebApplicationException
     */
    @Override
    public TroubleTicket readFrom(Class<TroubleTicket> type,
            Type type1,
            Annotation[] antns,
            MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream)
            throws IOException, WebApplicationException {

        TroubleTicket tt;

        System.out.println("=== TroubleTicket MessageBodyReader ENABLE ===");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readValue(entityStream, JsonNode.class);
        
        Set<TroubleTicketField> tokenList = new HashSet<TroubleTicketField>();
        
        // Iterate over first level to set received tokens
        Iterator<String> it = root.getFieldNames();
        while (it.hasNext()) {
            tokenList.add(TroubleTicketField.fromString(it.next()));
        }

        tt = mapper.readValue(root, TroubleTicket.class);
        tt.setFieldsIN(tokenList);

        return tt;
    }

}
