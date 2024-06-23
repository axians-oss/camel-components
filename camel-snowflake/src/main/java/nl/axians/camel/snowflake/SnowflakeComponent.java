package nl.axians.camel.snowflake;

import jakarta.annotation.Nonnull;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;

import java.util.Map;

/**
 * Represents the component that is responsible for creating {@link SnowflakeEndpoint} instances.
 */
@Component("snowflake")
public class SnowflakeComponent extends DefaultComponent {

    @Metadata
    private final SnowflakeConfiguration configuration = new SnowflakeConfiguration();

    @Override
    protected Endpoint createEndpoint(
            @Nonnull String theUri,
            @Nonnull String theRemaining,
            @Nonnull Map<String, Object> theParameters) throws Exception {
        // Use the component configuration and override it with endpoint specific configuration.
        final SnowflakeConfiguration config = configuration.copy();
        setProperties(config, theParameters);

        // Create a new Snowflake endpoint with the given URI, component, operation and configuration.
        return new SnowflakeEndpoint(theUri, this, SnowflakeOperation.valueOf(theRemaining), config);
    }

}
