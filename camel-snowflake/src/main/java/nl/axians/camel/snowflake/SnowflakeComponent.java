package nl.axians.camel.snowflake;

import jakarta.annotation.Nonnull;
import org.apache.camel.CamelContext;
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
    private SnowflakeConfiguration configuration;

    /**
     * Default constructor.
     */
    public SnowflakeComponent() {
    }

    /**
     * Constructor with a Camel context.
     *
     * @param theContext The Camel context.
     */
    public SnowflakeComponent(final CamelContext theContext) {
        super(theContext);
    }

    @Override
    protected Endpoint createEndpoint(
            @Nonnull String theUri,
            @Nonnull String theRemaining,
            @Nonnull Map<String, Object> theParameters) throws Exception {
        // Use the component configuration and override it with endpoint specific configuration.
        final SnowflakeConfiguration config = (configuration != null) ? configuration.copy() : new SnowflakeConfiguration();
        setProperties(config, theParameters);

        return new SnowflakeEndpoint(theUri, this, SnowflakeOperation.valueOf(theRemaining), config);
    }

    /**
     * Get the OAuth2 configuration to use for this component.
     *
     * @return The {@link SnowflakeConfiguration} to use for the component.
     */
    public SnowflakeConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Set the OAuth2 configuration to be used for this component.
     *
     * @param configuration The {@link SnowflakeConfiguration} to use for the component.
     */
    public void setConfiguration(SnowflakeConfiguration configuration) {
        this.configuration = configuration;
    }

}
