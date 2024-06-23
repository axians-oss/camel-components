package nl.axians.camel.snowflake;

import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;

/**
 * Configuration class for the Snowflake component.
 */
@UriParams
public class SnowflakeConfiguration implements Cloneable {

    @UriParam(label = "oauth", secret = true, description = "The OAuth2 client ID.")
    private String clientId;

    @UriParam(label = "oauth", secret = true, description = "The OAuth2 client secret.")
    private String clientSecret;

    @UriParam(label = "oauth", description = "The OAuth2 scope.")
    private String scope;

    @UriParam(label = "oauth", description = "The OAuth2 access token URL.")
    private String tokenUrl;

    @UriParam(label = "common", description = "The Snowflake API base URL.")
    private String baseUrl;

    @UriParam(label = "common", description = "The Snowflake database name to use.")
    private String database;

    @UriParam(label = "common", description = "The Snowflake warehouse to use.")
    private String warehouse;

    @UriParam(label = "common", description = "The Snowflake role to apply.")
    private String role;

    @UriParam(label = "common", description = "The Snowflake schema to use.")
    private String schema;

    /**
     * Gets the OAuth2 client ID.
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
     * Gets the OAuth2 client secret.
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
     * Gets the OAuth2 scope.
     *
     * @return The OAuth2 scope.
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the OAuth2 scope.
     *
     * @param theScope The OAuth2 scope.
     */
    public void setScope(String theScope) {
        scope = theScope;
    }

    /**
     * Gets the OAuth2 access token URL.
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
     * Gets the Snowflake API base URL.
     *
     * @return The Snowflake API base URL.
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the Snowflake API base URL.
     *
     * @param theBaseUrl The Snowflake API base URL.
     */
    public void setBaseUrl(String theBaseUrl) {
        baseUrl = theBaseUrl;
    }

    /**
     * Gets the Snowflake database name.
     *
     * @return The Snowflake database name.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Sets the Snowflake database name.
     *
     * @param theDatabase The Snowflake database name.
     */
    public void setDatabase(String theDatabase) {
        database = theDatabase;
    }

    /**
     * Gets the Snowflake warehouse.
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
     * Gets the Snowflake role.
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
     * Gets the Snowflake schema.
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

    /**
     * Copy the configuration.
     *
     * @return A copy of the configuration.
     */
    public SnowflakeConfiguration copy() {
        try {
            return  (SnowflakeConfiguration) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


}