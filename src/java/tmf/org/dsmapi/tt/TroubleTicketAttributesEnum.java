package tmf.org.dsmapi.tt;

public enum TroubleTicketAttributesEnum {

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

    TroubleTicketAttributesEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static TroubleTicketAttributesEnum fromString(String text) {
        if (text != null) {
            for (TroubleTicketAttributesEnum b : TroubleTicketAttributesEnum.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}
