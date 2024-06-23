package nl.axians.camel.snowflake;

import jakarta.annotation.Nonnull;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

import java.net.URI;
import java.net.http.HttpRequest;

import static nl.axians.camel.snowflake.Snowflake.SNOWFLAKE_STATEMENT_HANDLE;

/**
 * The Snowflake producer, which sends messages to the Snowflake API.
 */
public class SnowflakeProducer extends DefaultProducer {

    private final SnowflakeEndpoint endpoint;

    public SnowflakeProducer(@Nonnull final SnowflakeEndpoint theEndpoint) {
        super(theEndpoint);
        endpoint = theEndpoint;
    }

    @Override
    public void process(final @Nonnull Exchange theExchange) throws Exception {
        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json");

        String url = endpoint.getConfiguration().getBaseUrl();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (endpoint.getOperation()) {
            case SubmitStatements: {
                final URI uri = new URI(url + "/statements");
                requestBuilder.uri(uri).POST(HttpRequest.BodyPublishers.ofString(theExchange.getIn().getBody(String.class)));
                break;
            }
            case CheckStatementStatus: {
                final String statementHandle = theExchange.getIn().getHeader(SNOWFLAKE_STATEMENT_HANDLE, String.class);
                final URI uri = new URI(url + "/statements/" + statementHandle);
                requestBuilder.uri(uri).GET();
                break;
            }
            case CancelStatement:
                final String statementHandle = theExchange.getIn().getHeader(SNOWFLAKE_STATEMENT_HANDLE, String.class);
                final URI uri = new URI(url + "/statements/" + statementHandle + "/cancel");
                requestBuilder.uri(uri).POST(HttpRequest.BodyPublishers.noBody());
                break;
            default:
                throw new UnsupportedOperationException("Operation not supported: " + endpoint.getOperation());
        }

        endpoint.getClient().sendRequest(requestBuilder.build());
    }

}
