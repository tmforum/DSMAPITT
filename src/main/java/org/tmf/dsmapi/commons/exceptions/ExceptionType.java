package org.tmf.dsmapi.commons.exceptions;

/**
 *
 * @author maig7313
 */
public enum ExceptionType {

    /**
     *
     */
    BAD_USAGE_GENERIC(new ExceptionBean("4000", "Bad Usage")),
    /**
     *
     */
    BAD_USAGE_SEARCH_QUERY(new ExceptionBean("4001", "Search query is not valid")),
    /**
     *
     */
    BAD_USAGE_FLOW_TRANSITION(new ExceptionBean("4002", "Workflow, state transition is not valid")),
    /**
     *
     */
    BAD_USAGE_FLOW_UNKNOWN_STATE(new ExceptionBean("4004", "Workflow, unknown state")),
    /**
     *
     */
    BAD_USAGE_MANDATORY_FIELDS(new ExceptionBean("4003", "Missing mandatory field")),
    /**
     *
     */
    BAD_USAGE_UNKNOWN_VALUE(new ExceptionBean("E160", "Unknown value")),
    /**
     *
     */
    BAD_USAGE_OPERATOR(new ExceptionBean("4011", "Wrong operator usage")),
    /**
     *
     */
    BAD_USAGE_FORMAT(new ExceptionBean("4012", "Wrong format")),
    /**
     *
     */
    TECHNICAL(new ExceptionBean("9000", "Technical error")),
    /**
     *
     */
    UNKNOWN_RESOURCE(new ExceptionBean("4041", "Unknown resource"));
    private ExceptionBean info;

    ExceptionType(ExceptionBean info) {
        this.info = info;
    }

    @Override
    public String toString() {
        String out = String.format("%1$ - %2$ - %3$", this.getInfo().getCode(), this.name(), this.getInfo().getTitle());
        return out;
    }

    /**
     *
     * @return
     */
    public ExceptionBean getInfo() {
        return info;
    }
}
