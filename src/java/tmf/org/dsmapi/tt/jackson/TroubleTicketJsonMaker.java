package tmf.org.dsmapi.tt.jackson;

import java.util.Set;
import javax.ws.rs.core.PathSegment;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import tmf.org.dsmapi.tt.model.Note;
import tmf.org.dsmapi.tt.model.RelatedObject;
import tmf.org.dsmapi.tt.model.RelatedParty;
import tmf.org.dsmapi.tt.model.TroubleTicket;
import tmf.org.dsmapi.tt.model.TroubleTicketField;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.CORRELATION_ID;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.CREATION_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.DESCRIPTION;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.NOTES;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.RELATED_OBJECTS;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.RELATED_PARTIES;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.RESOLUTION_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.SEVERITY;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.STATUS;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.STATUS_CHANGE_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.STATUS_CHANGE_REASON;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.SUB_STATUS;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.TARGET_RESOLUTION_DATE;
import static tmf.org.dsmapi.tt.model.TroubleTicketField.TYPE;

public class TroubleTicketJsonMaker {

    public static ObjectNode getView(TroubleTicket tt, Set<TroubleTicketField> fields) {
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        // id mandatory
        root.put(TroubleTicketField.ID.getText(), tt.getId());

        ArrayNode arrayNode;
        for (TroubleTicketField field : fields) {
            switch (field) {
                case CORRELATION_ID:
                    root.put(CORRELATION_ID.getText(), tt.getCorrelationId());
                    break;
                case CREATION_DATE:
                    root.put(CREATION_DATE.getText(), tt.getCreationDate());
                    break;
                case DESCRIPTION:
                    root.put(DESCRIPTION.getText(), tt.getDescription());
                    break;
                case NOTES:
                    arrayNode = root.putArray(NOTES.getText());
                    for (Note pojo : tt.getNotes()) {
                        arrayNode.addPOJO(pojo);
                    }
                    break;
                case RELATED_OBJECTS:
                    arrayNode = root.putArray(RELATED_OBJECTS.getText());
                    for (RelatedObject pojo : tt.getRelatedObjects()) {
                        arrayNode.addPOJO(pojo);
                    }
                    break;
                case RELATED_PARTIES:
                    arrayNode = root.putArray(RELATED_PARTIES.getText());
                    for (RelatedParty pojo : tt.getRelatedParties()) {
                        arrayNode.addPOJO(pojo);
                    }
                    break;
                case RESOLUTION_DATE:
                    root.put(RESOLUTION_DATE.getText(), tt.getResolutionDate());
                    break;
                case SEVERITY:
                    root.put(SEVERITY.getText(), tt.getSeverity().toString());
                    break;
                case STATUS:
                    root.put(STATUS.getText(), tt.getStatus().toString());
                    break;
                case STATUS_CHANGE_DATE:
                    root.put(STATUS_CHANGE_DATE.getText(), tt.getStatusChangeDate());
                    break;
                case STATUS_CHANGE_REASON:
                    root.put(STATUS_CHANGE_REASON.getText(), tt.getStatusChangeReason());
                    break;
                case SUB_STATUS:
                    root.put(SUB_STATUS.getText(), tt.getSubStatus().toString());
                    break;
                case TARGET_RESOLUTION_DATE:
                    root.put(TARGET_RESOLUTION_DATE.getText(), tt.getResolutionDate());
                    break;
                case TYPE:
                    root.put(TYPE.getText(), tt.getType());
                    break;
            }
        }

        return root;

    }
}
