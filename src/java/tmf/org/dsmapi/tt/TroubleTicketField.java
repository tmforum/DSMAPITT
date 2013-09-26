package tmf.org.dsmapi.tt;

import java.util.HashSet;
import java.util.Set;

public enum TroubleTicketField {

    ALL("all"),
    ID("id"),
    CORRELATION_ID("correlationId"),
    CREATION_DATE("creationDate"),
    DESCRIPTION("description"),
    NOTES("notes"),
    RELATED_OBJECTS("relatedObjects"),
    RELATED_PARTIES("relatedParties"),
    RESOLUTION_DATE("resolutionDate"),
    SEVERITY("severity"),
    STATUS("status"),
    STATUS_CHANGE_DATE("statusChangeDate"),
    STATUS_CHANGE_REASON("statusChangeReason"),
    SUB_STATUS("subStatus"),
    TARGET_RESOLUTION_DATE("targetResolutionDate"),
    TYPE("type");
    private String text;

    TroubleTicketField(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static TroubleTicketField fromString(String text) {
        if (text != null) {
            for (TroubleTicketField b : TroubleTicketField.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }

    public static Set<TroubleTicketField> fromStringToSet(String fields) {
        // Convert fields parameter to a set of TroubleTicketField
        Set<TroubleTicketField> fieldsSet = new HashSet<TroubleTicketField>();
        TroubleTicketField fieldName;
        if (fields != null) {
            String[] tokenArray = fields.split(",");
            for (int i = 0; i < tokenArray.length; i++) {
                fieldName = TroubleTicketField.fromString(tokenArray[i]);
                // Avoid to add null when fieldName doesn't exist
                if (fieldName != null) {
                    fieldsSet.add(fieldName);
                }
            }
        } else {
            // ALL
            fieldsSet.add(TroubleTicketField.ALL);
        }

        return fieldsSet;
    }
}
