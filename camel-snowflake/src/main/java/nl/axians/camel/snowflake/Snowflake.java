package nl.axians.camel.snowflake;

import jakarta.annotation.Nonnull;

/**
 * Snowflake component utility class.
 */
public final class Snowflake {

    public static final String SNOWFLAKE_COMPONENT_NAME = "snowflake";

    public static final String SNOWFLAKE_STATEMENT_HANDLE = "SnowflakeStatementHandle";
    public static final String SNOWFLAKE_REQUEST_ID = "SnowflakeRequestId";
    public static final String SNOWFLAKE_PARTITION = "SnowflakePartition";
    public static final String SNOWFLAKE_STATEMENT_COUNT = "SnowflakeStatementCount";
    public static final String SNOWFLAKE_RETRY = "SnowflakeRetry";

    /**
     * Constructor. Private to prevent instantiation.
     */
    private Snowflake() {
    }

    /**
     * Creates a new Snowflake URI builder.
     *
     * @return The Snowflake URI builder.
     */
    public static URIBuilder uri() {
        return new URIBuilder();
    }

    /**
     * Creates a new Snowflake URI builder for specific operation.
     *
     * @param theOperation The operation to perform.
     * @return The Snowflake URI builder.
     */
    public static URIBuilder uri(@Nonnull final SnowflakeOperation theOperation) {
        return new URIBuilder().operation(theOperation);
    }

    /**
     * A Snowflake URI builder.
     */
    public static class URIBuilder {

        private SnowflakeOperation operation;
        private String clientId;
        private String clientSecret;
        private String scope;
        private String tokenUrl;
        private String baseUrl;
        private String database;
        private String warehouse;
        private String role;
        private String schema;
        private Long timeoutSecs;
        private boolean async = false;

        /**
         * Sets the operation to perform.
         *
         * @param theOperation The operation to perform.
         * @return The Snowflake URI builder.
         */
        public URIBuilder operation(@Nonnull final SnowflakeOperation theOperation) {
            operation = theOperation;
            return this;
        }

        /**
         * Sets the OAuth2 client ID.
         *
         * @param theClientId The OAuth2 client ID.
         * @return The Snowflake URI builder.
         */
        public URIBuilder clientId(final String theClientId) {
            clientId = theClientId;
            return this;
        }

        /**
         * Sets the OAuth2 client secret.
         *
         * @param theClientSecret The OAuth2 client secret.
         * @return The Snowflake URI builder.
         */
        public URIBuilder clientSecret(final String theClientSecret) {
            clientSecret = theClientSecret;
            return this;
        }

        /**
         * Sets the OAuth2 scope.
         *
         * @param theScope The OAuth2 scope.
         * @return The Snowflake URI builder.
         */
        public URIBuilder scope(final String theScope) {
            scope = theScope;
            return this;
        }

        /**
         * Sets the OAuth2 token URL.
         *
         * @param theTokenUrl The OAuth2 token URL.
         * @return The Snowflake URI builder.
         */
        public URIBuilder tokenUrl(final String theTokenUrl) {
            tokenUrl = theTokenUrl;
            return this;
        }

        /**
         * Sets the Snowflake base URL.
         *
         * @param theBaseUrl The Snowflake base URL.
         * @return The Snowflake URI builder.
         */
        public URIBuilder baseUrl(final String theBaseUrl) {
            baseUrl = theBaseUrl;
            return this;
        }

        /**
         * Sets the Snowflake database.
         *
         * @param theDatabase The Snowflake database.
         * @return The Snowflake URI builder.
         */
        public URIBuilder database(final String theDatabase) {
            database = theDatabase;
            return this;
        }

        /**
         * Sets the Snowflake warehouse.
         *
         * @param theWarehouse The Snowflake warehouse.
         * @return The Snowflake URI builder.
         */
        public URIBuilder warehouse(final String theWarehouse) {
            warehouse = theWarehouse;
            return this;
        }

        /**
         * Sets the Snowflake role.
         *
         * @param theRole The Snowflake role.
         * @return The Snowflake URI builder.
         */
        public URIBuilder role(final String theRole) {
            role = theRole;
            return this;
        }

        /**
         * Sets the Snowflake schema.
         *
         * @param theSchema The Snowflake schema.
         * @return The Snowflake URI builder.
         */
        public URIBuilder schema(final String theSchema) {
            schema = theSchema;
            return this;
        }

        /**
         * Sets the statement timeout in seconds.
         *
         * @param theTimeoutSecs The statement timeout in seconds.
         * @return The Snowflake URI builder.
         */
        public URIBuilder timeoutSecs(final long theTimeoutSecs) {
            timeoutSecs = theTimeoutSecs;
            return this;
        }

        /**
         * Sets whether statement should be executed asynchronous.
         *
         * @param theAsync Whether statement should be executed asynchronous.
         * @return The Snowflake URI builder.
         */
        public URIBuilder async(final boolean theAsync) {
            async = theAsync;
            return this;
        }

        /**
         * Builds the Camel URI.
         *
         * @return The Camel URI.
         */
        public String build() {
            final StringBuilder query = new StringBuilder();

            if (operation == null) {
                throw new IllegalArgumentException("Operation must be set");
            }

            if (clientId != null && !clientId.isBlank()) {
                query.append("clientId=");
                query.append(clientId);
                query.append("&");
            }

            if (clientSecret != null && !clientSecret.isBlank()) {
                query.append("clientSecret=");
                query.append(clientSecret);
                query.append("&");
            }

            if (scope != null && !scope.isBlank()) {
                query.append("scope=");
                query.append(scope);
                query.append("&");
            }

            if (tokenUrl != null && !tokenUrl.isBlank()) {
                query.append("tokenUrl=");
                query.append(tokenUrl);
                query.append("&");
            }

            if (baseUrl != null && !baseUrl.isBlank()) {
                query.append("baseUrl=");
                query.append(baseUrl);
                query.append("&");
            }

            if (database != null && !database.isBlank()) {
                query.append("database=");
                query.append(database);
                query.append("&");
            }

            if (warehouse != null && !warehouse.isBlank()) {
                query.append("warehouse=");
                query.append(warehouse);
                query.append("&");
            }

            if (role != null && !role.isBlank()) {
                query.append("role=");
                query.append(role);
                query.append("&");
            }

            if (schema != null && !schema.isBlank()) {
                query.append("schema=");
                query.append(schema);
                query.append("&");
            }

            if (timeoutSecs != null) {
                query.append("timeoutSecs=");
                query.append(timeoutSecs);
                query.append("&");
            }

            if (async) {
                query.append("async=true");
            }

            // Remove trailing '&' if present.
            if (!query.isEmpty() && query.charAt(query.length() - 1) == '&') {
                query.deleteCharAt(query.length() - 1);
            }

            return SNOWFLAKE_COMPONENT_NAME + ":" + operation + (query.isEmpty() ? "" : "?" + query);
        }
    }

}
