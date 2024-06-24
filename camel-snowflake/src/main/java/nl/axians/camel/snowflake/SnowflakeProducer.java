package nl.axians.camel.snowflake;

import jakarta.annotation.Nonnull;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static nl.axians.camel.snowflake.Snowflake.SNOWFLAKE_STATEMENT_HANDLE;

/**
 * The Snowflake producer, which sends messages to the Snowflake API.
 */
public class SnowflakeProducer extends DefaultProducer {

    static final String REQUEST_BODY = """
            {
                "warehouse": "%s",
                "database": "%s",
                "schema": "%s",
                "statement": "%s",
                "role": "%s",
            }""";

    private final SnowflakeEndpoint endpoint;

    public SnowflakeProducer(@Nonnull final SnowflakeEndpoint theEndpoint) {
        super(theEndpoint);
        endpoint = theEndpoint;
    }

    @Override
    public void process(final @Nonnull Exchange theExchange) throws Exception {
        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        String url = endpoint.getConfiguration().getBaseUrl();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        switch (endpoint.getOperation()) {
            case SubmitStatement: {
                final URI uri = new URI(url + "/statements");
                final String requestBody = String.format(REQUEST_BODY,
                        endpoint.getConfiguration().getWarehouse(),
                        endpoint.getConfiguration().getDatabase(),
                        endpoint.getConfiguration().getSchema(),
                        theExchange.getIn().getBody(String.class),
                        endpoint.getConfiguration().getRole());
                requestBuilder.uri(uri).POST(HttpRequest.BodyPublishers.ofString(requestBody));
                break;
            }
            case CheckStatementStatus: {
                final String statementHandle = theExchange.getIn().getHeader(SNOWFLAKE_STATEMENT_HANDLE, String.class);
                if (statementHandle == null || statementHandle.isEmpty())
                    throw new SnowflakeException(SNOWFLAKE_STATEMENT_HANDLE + " header is missing or empty: Statement handle is required for operation: " + endpoint.getOperation());

                final URI uri = new URI(url + "/statements/" + statementHandle);
                requestBuilder.uri(uri).GET();
                break;
            }
            case CancelStatement:
                final String statementHandle = theExchange.getIn().getHeader(SNOWFLAKE_STATEMENT_HANDLE, String.class);
                if (statementHandle == null || statementHandle.isEmpty())
                    throw new SnowflakeException(SNOWFLAKE_STATEMENT_HANDLE + " header is missing or empty: Statement handle is required for operation: " + endpoint.getOperation());

                final URI uri = new URI(url + "/statements/" + statementHandle + "/cancel");
                requestBuilder.uri(uri).POST(HttpRequest.BodyPublishers.noBody());
                break;
            default:
                throw new UnsupportedOperationException("Operation not supported: " + endpoint.getOperation());
        }

        final HttpResponse<String> response = endpoint.getClient().sendRequest(requestBuilder.build());
        theExchange.getMessage().setBody(response.body());
    }

}
