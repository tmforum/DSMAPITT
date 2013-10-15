package tmf.org.dsmapi.tt.service.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

@Provider
@Produces("application/json")
public class JacksonConfigurator implements ContextResolver<ObjectMapper> {

    private ObjectMapper mapper = new ObjectMapper();
    
    public JacksonConfigurator() {
        mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    @Override
    public ObjectMapper getContext(Class<?> arg0) {
        return mapper;
    }

}