package nl.axians.camel.snowflake.model;

import java.time.Instant;

public class QueryFailureStatus {

    private String code;
    private String sqlState;
    private String message;
    private String statementHandle;
    private Instant createdOn;
    private String statementStatusUrl;

}
