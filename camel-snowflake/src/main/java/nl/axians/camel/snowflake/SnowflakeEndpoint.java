package nl.axians.camel.snowflake;

import jakarta.annotation.Nonnull;
import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;

/**
 * Represents a Snowflake endpoint, which is capable of sending messages to the Snowflake API by creating a
 * {@link SnowflakeProducer} instances.
 */
@UriEndpoint(firstVersion = "1.0.0", scheme = "snowflake", title = "Snowflake", syntax = "snowflake:operation",
        producerOnly = true, category = {Category.API, Category.DATABASE})
public class SnowflakeEndpoint extends DefaultEndpoint {


    @UriParam
    @Metadata(required = true)
    private final SnowflakeConfiguration configuration;

    @UriPath
    private final SnowflakeOperation operation;

    @UriParam(label = "common", description = "Statement timeout in seconds.")
    private Long timeoutSecs;

    @UriParam(label = "common", description = "Whether statement should be executed asynchronous.", defaultValue = "false")
    private Boolean async = false;


    private final SnowflakeClient client;

    public SnowflakeEndpoint(
            @Nonnull String theUri,
            @Nonnull SnowflakeComponent theComponent,
            @Nonnull SnowflakeOperation theOperation,
            @Nonnull SnowflakeConfiguration theConfiguration) {
        super(theUri, theComponent);
        configuration = theConfiguration;
        operation = theOperation;
        client = new SnowflakeClient(configuration);
    }

    @Override
    public Producer createProducer() {
        return new SnowflakeProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) {
        throw new UnsupportedOperationException("Consumer not supported for Snowflake endpoint");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * Get the Snowflake configuration to use for this endpoint.
     *
     * @return The {@link SnowflakeConfiguration} to use for the endpoint.
     */
    public SnowflakeConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Get the Snowflake operation to use for this endpoint.
     *
     * @return The {@link SnowflakeOperation} to use for the endpoint.
     */
    public SnowflakeOperation getOperation() {
        return operation;
    }

    /**
     * Get the Snowflake client to use for this endpoint.
     *
     * @return The {@link SnowflakeClient} to use for the endpoint.
     */
    public SnowflakeClient getClient() {
        return client;
    }

    /**
     * Get the statement timeout in seconds.
     *
     * @return The statement timeout in seconds.
     */
    public Long getTimeoutSecs() {
        return timeoutSecs;
    }

    /**
     * Set the statement timeout in seconds.
     *
     * @param theTimeoutSecs The statement timeout in seconds.
     */
    public void setTimeoutSecs(Long theTimeoutSecs) {
        timeoutSecs = theTimeoutSecs;
    }

    /**
     * Get whether statement should be executed asynchronous.
     *
     * @return Whether statement should be executed asynchronous.
     */
    public Boolean getAsync() {
        return async;
    }

    /**
     * Set whether statement should be executed asynchronous.
     *
     * @param theAsync Whether statement should be executed asynchronous.
     */
    public void setAsync(Boolean theAsync) {
        async = theAsync;
    }

}
