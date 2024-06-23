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

}
