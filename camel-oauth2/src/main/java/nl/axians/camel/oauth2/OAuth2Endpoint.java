package nl.axians.camel.oauth2;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.support.DefaultEndpoint;

@UriEndpoint(firstVersion = "1.0.0", scheme = "oauth2", title = "OAuth2", syntax = "oauth2:operation",
        producerOnly = true, category = {Category.API, Category.SECURITY})
public class OAuth2Endpoint extends DefaultEndpoint {

    @UriParam
    @Metadata(required = true)
    private final OAuth2Configuration configuration;

    /**
     * Create a new OAuth2Endpoint with the given uri and component.
     *
     * @param uri The endpoint uri.
     * @param component The {@link OAuth2Component} that created this endpoint.
     * @param configuration The {@link OAuth2Configuration} to use.
     */
    public OAuth2Endpoint(final String uri,
                          final OAuth2Component component,
                          final OAuth2Configuration configuration) {
        super(uri, component);
        this.configuration = configuration;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new OAuth2Producer(this);
    }

    @Override
    public Consumer createConsumer(final Processor processor) {
        throw new UnsupportedOperationException("Consumer not supported for camel-oauth2 component");
    }

    /**
     * Get the OAuth2 configuration to use for this endpoint.
     *
     * @return The {@link OAuth2Configuration} to use for the endpoint.
     */
    public OAuth2Configuration getConfiguration() {
        return configuration;
    }

}
