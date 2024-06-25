package nl.axians.camel.snowflake;

import jakarta.annotation.Nonnull;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static nl.axians.camel.snowflake.Snowflake.*;
import static nl.axians.camel.snowflake.SnowflakeOperation.*;

/**
 * The Snowflake producer, which sends messages to the Snowflake API.
 */
public class SnowflakeProducer extends DefaultProducer {

    // TODO Timeout if specified in the URI.
    // TODO Statement count if specified in the URI. Otherwise, default to 1.
    static final String REQUEST_BODY = """
            {
                "warehouse": "%s",
                "database": "%s",
                "schema": "%s",
                "statement": "%s",
                "role": "%s",
                "parameters": {
                    "MULTI_STATEMENT_COUNT": "%s"
                }
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
                String count = theExchange.getIn().getHeader(SNOWFLAKE_STATEMENT_COUNT, String.class);
                if (count == null || count.isEmpty())
                    count = "1";

                final URI uri = new URI(url + "/statements" + getQueryParameters(SubmitStatement, theExchange));
                final String requestBody = String.format(REQUEST_BODY,
                        endpoint.getConfiguration().getWarehouse(),
                        endpoint.getConfiguration().getDatabase(),
                        endpoint.getConfiguration().getSchema(),
                        theExchange.getIn().getBody(String.class),
                        endpoint.getConfiguration().getRole(),
                        count);
                requestBuilder.uri(uri).POST(HttpRequest.BodyPublishers.ofString(requestBody));
                break;
            }
            case CheckStatementStatus: {
                final String statementHandle = theExchange.getIn().getHeader(SNOWFLAKE_STATEMENT_HANDLE, String.class);
                if (statementHandle == null || statementHandle.isEmpty())
                    throw new SnowflakeException(SNOWFLAKE_STATEMENT_HANDLE + " header is missing or empty: Statement handle is required for operation: " + endpoint.getOperation());

                final URI uri = new URI(url + "/statements/" + statementHandle + getQueryParameters(CheckStatementStatus, theExchange));
                requestBuilder.uri(uri).GET();
                break;
            }
            case CancelStatement:
                final String statementHandle = theExchange.getIn().getHeader(SNOWFLAKE_STATEMENT_HANDLE, String.class);
                if (statementHandle == null || statementHandle.isEmpty())
                    throw new SnowflakeException(SNOWFLAKE_STATEMENT_HANDLE + " header is missing or empty: Statement handle is required for operation: " + endpoint.getOperation());

                final URI uri = new URI(url + "/statements/" + statementHandle + "/cancel" + getQueryParameters(CancelStatement, theExchange));
                requestBuilder.uri(uri).POST(HttpRequest.BodyPublishers.noBody());
                break;
            default:
                throw new UnsupportedOperationException("Operation not supported: " + endpoint.getOperation());
        }

        final HttpResponse<String> response = endpoint.getClient().sendRequest(requestBuilder.build());
        theExchange.getMessage().setBody(response.body());
    }

    /**
     * Gets the query parameters for the given operation.
     *
     * @param theOperation The operation.
     * @return The query parameters.
     */
    private String getQueryParameters(
            @Nonnull final SnowflakeOperation theOperation,
            @Nonnull final Exchange theExchange) {
        final Map<String, Object> headers = theExchange.getIn().getHeaders();
        final StringBuilder queryParameters = new StringBuilder();

        if (headers.containsKey(SNOWFLAKE_REQUEST_ID)) {
            queryParameters.append("&requestId=").append(headers.get(SNOWFLAKE_REQUEST_ID));
        }

        switch (theOperation) {
            case SubmitStatement:
                // Add optional parameters for submit statement.
                if (headers.containsKey(SNOWFLAKE_RETRY)) {
                    queryParameters.append("&retry=").append(headers.get(SNOWFLAKE_RETRY));
                }

                if (endpoint.getAsync()) {
                    queryParameters.append("&async=true");
                }
                break;
            case CheckStatementStatus:
                // Add optional parameters for check statement status.
                if (headers.containsKey(SNOWFLAKE_PARTITION)) {
                    queryParameters.append("&partition=").append(headers.get(SNOWFLAKE_PARTITION));
                }
                break;
            case CancelStatement:
                // No optional parameters for cancel statement.
                break;
            default:
                return "";
        }

        String query = queryParameters.toString();
        if (!query.isEmpty())
            query = "?" + query.substring(1);

        return query;
    }

}
