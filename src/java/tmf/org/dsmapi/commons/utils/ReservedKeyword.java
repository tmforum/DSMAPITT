package tmf.org.dsmapi.commons.utils;

public enum ReservedKeyword {

    QUERY_KEY_FIELD(":fields"),
    QUERY_KEY_FIELD_2(
    "fields");
    private String text;

    ReservedKeyword(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static ReservedKeyword fromString(String text) {
        if (text != null) {
            for (ReservedKeyword b : ReservedKeyword.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}
