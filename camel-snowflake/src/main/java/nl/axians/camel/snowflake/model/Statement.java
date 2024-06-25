package nl.axians.camel.snowflake.model;

import java.util.Map;

public class Statement {

    private String statement;
    private Long timeout;
    private String database;
    private String schema;
    private String warehouse;
    private String role;
    private Map<Integer, Binding> bindings;
    private Parameters parameters;

}
