package nl.axians.camel.snowflake.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "camel.component.snowflake")
public class SnowflakeComponentConfiguration {

    private String clientId;
    private String clientSecret;
    private String scope;
    private String tokenUrl;
    private String baseUrl;
    private String database;
    private String warehouse;
    private String role;
    private String schema;

    /**
     * The OAuth2 client ID.
     *
     * @return The OAuth2 client ID.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the OAuth2 client ID.
     *
     * @param theClientId The OAuth2 client ID.
     */
    public void setClientId(String theClientId) {
        clientId = theClientId;
    }

    /**
     * The OAuth2 client secret.
     *
     * @return The OAuth2 client secret.
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Sets the OAuth2 client secret.
     *
     * @param theClientSecret The OAuth2 client secret.
     */
    public void setClientSecret(String theClientSecret) {
        clientSecret = theClientSecret;
    }

    /**
     * The OAuth2 scope.
     *
     * @return The OAuth2 scope.
     */
    public String getScope() {
        return scope;
    }

    /**
     * The OAuth2 scope.
     *
     * @param theScope The OAuth2 scope.
     */
    public void setScope(String theScope) {
        scope = theScope;
    }

    /**
     * The OAuth2 access token URL.
     *
     * @return The OAuth2 access token URL.
     */
    public String getTokenUrl() {
        return tokenUrl;
    }

    /**
     * Sets the OAuth2 access token URL.
     *
     * @param theTokenUrl The OAuth2 access token URL.
     */
    public void setTokenUrl(String theTokenUrl) {
        tokenUrl = theTokenUrl;
    }

    /**
     * The Snowflake API base URL.
     *
     * @return The Snowflake API base URL.
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Set the Snowflake API base URL.
     *
     * @param theBaseUrl The Snowflake API base URL.
     */
    public void setBaseUrl(String theBaseUrl) {
        baseUrl = theBaseUrl;
    }

    /**
     * The Snowflake database name.
     *
     * @return The Snowflake database name.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Set the Snowflake database name.
     *
     * @param theDatabase The Snowflake database name.
     */
    public void setDatabase(String theDatabase) {
        database = theDatabase;
    }

    /**
     * The Snowflake warehouse.
     *
     * @return The Snowflake warehouse.
     */
    public String getWarehouse() {
        return warehouse;
    }

    /**
     * Sets the Snowflake warehouse.
     *
     * @param theWarehouse The Snowflake warehouse.
     */
    public void setWarehouse(String theWarehouse) {
        warehouse = theWarehouse;
    }

    /**
     * The Snowflake role.
     *
     * @return The Snowflake role.
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the Snowflake role.
     *
     * @param theRole The Snowflake role.
     */
    public void setRole(String theRole) {
        role = theRole;
    }

    /**
     * The Snowflake schema.
     *
     * @return The Snowflake schema.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Sets the Snowflake schema.
     *
     * @param theSchema The Snowflake schema.
     */
    public void setSchema(String theSchema) {
        schema = theSchema;
    }

}
