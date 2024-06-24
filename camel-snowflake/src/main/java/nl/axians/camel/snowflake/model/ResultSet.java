package nl.axians.camel.snowflake.model;

import java.time.Instant;
import java.util.List;

public class ResultSet {

    private String code;
    private String sqlState;
    private String message;
    private String statementHandle;
    private List<String> statementHandles;
    private Instant createdOn;
    private String statementStatusUrl;
    private ResultSetMetaData resultSetMetaData;
    private List<List<Object>> data;
    private ResultSetStats stats;

}
