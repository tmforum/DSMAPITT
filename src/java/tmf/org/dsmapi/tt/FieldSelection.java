package tmf.org.dsmapi.tt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.PathSegment;
import tmf.org.dsmapi.tt.model.TroubleTicketField;

public class FieldSelection {

    public static Set<TroubleTicketField> getFields(List<String> fields) {
        
        Set<TroubleTicketField> set = new HashSet<TroubleTicketField>();
        for (String fieldSelector : fields) {
            set.addAll(TroubleTicketField.fromStringToSet(fieldSelector));
        }        
        return set;
        
    }

    @Deprecated
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

    @Deprecated
    public static Set<TroubleTicketField> getFields(PathSegment pathSegment) {
        String path = pathSegment.getPath();
        Set<TroubleTicketField> fieldSet = TroubleTicketField.fromStringToSet(path);
        return fieldSet;
    }
}
