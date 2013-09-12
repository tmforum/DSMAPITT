package tmf.org.dsmapi.tt.jaxrs;

import java.util.Set;
import javax.ws.rs.core.PathSegment;
import tmf.org.dsmapi.tt.model.TroubleTicketField;

public class FieldSelection {

    public static boolean isUsedInPath(PathSegment pathSegment) {

        boolean isUsed = false;
        String path = pathSegment.getPath();

        int numTokens = 0;
        if (path != null) {

            String[] tokenArray = path.split(",");
            numTokens = tokenArray.length;
            //If one check if it is a valid attribute then it is attribute selection
            //otherwise use it an id
            if (numTokens == 1) {
                if (TroubleTicketField.fromString(path) != null) {
                    isUsed = true;
                }
            } else if (numTokens > 1) {
                isUsed = true;
            }

        }

        return isUsed;
    }

    public static Set<TroubleTicketField> getFields(PathSegment pathSegment) {
        String path = pathSegment.getPath();
        Set<TroubleTicketField> fieldSet = TroubleTicketField.fromStringToSet(path);
        return fieldSet;
    }
}
