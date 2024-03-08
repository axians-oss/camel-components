package nl.axians.camel.oauth2;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;

import java.util.Map;

@Component("oauth2")
public class OAuth2Component extends DefaultComponent {

    @Metadata
    private OAuth2Configuration configuration = new OAuth2Configuration();

    public OAuth2Component() {
    }

    public OAuth2Component(final CamelContext context) {
        super(context);
    }

    @Override
    protected Endpoint createEndpoint(final String uri,
                                      final String remaining,
                                      final Map<String, Object> parameters) throws Exception {
        if (remaining == null || remaining.isEmpty()) {
            throw new IllegalArgumentException("Remaining part of the URI cannot be empty");
        }

        final OAuth2Configuration configuration = this.configuration != null ?
                this.configuration.copy() : new OAuth2Configuration();
        configuration.setName(remaining);

        final Endpoint endpoint = new OAuth2Endpoint(uri, this, configuration);
        setProperties(endpoint, parameters);

        return endpoint;
    }

    /**
     * Get the OAuth2 configuration to use for this component.
     *
     * @return The {@link OAuth2Configuration} to use for the component.
     */
    public OAuth2Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Set the OAuth2 configuration to be used for this component.
     *
     * @param configuration The {@link OAuth2Configuration} to use for the component.
     */
    public void setConfiguration(OAuth2Configuration configuration) {
        this.configuration = configuration;
    }

}
