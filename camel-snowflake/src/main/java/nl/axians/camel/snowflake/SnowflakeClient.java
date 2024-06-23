package nl.axians.camel.snowflake;

import jakarta.annotation.Nonnull;
import nl.axians.camel.http.common.oauth.OAuthTokenManager;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The Snowflake client, which is used to interact with the Snowflake API.
 */
public class SnowflakeClient {

    private final HttpClient httpClient;
    private final OAuthTokenManager tokenManager;

    /**
     * Constructor.
     *
     * @param theConfiguration The configuration for the Snowflake client.
     */
    public SnowflakeClient(@Nonnull final SnowflakeConfiguration theConfiguration) {
        httpClient = HttpClient.newHttpClient();
        tokenManager = new OAuthTokenManager(httpClient, theConfiguration.getTokenUrl(),
                theConfiguration.getClientId(), theConfiguration.getClientSecret(), theConfiguration.getScope());
    }

    /**
     * Sends a request to the Snowflake API. Automatically fetches a new token if the request returns a 401 status.
     *
     * @param theRequest The request to send.
     * @return The response from the Snowflake API.
     * @throws Exception If an error occurred while sending the request.
     */
    public HttpResponse<String> sendRequest(@Nonnull final HttpRequest theRequest) throws Exception {
        HttpRequest requestWithAuth = addAuthorization(theRequest);
        HttpResponse<String> response = httpClient.send(requestWithAuth, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            // If the request returned a 401 status, fetch a new token and retry the request.
            tokenManager.fetchToken();
            requestWithAuth = addAuthorization(theRequest);
            response = httpClient.send(requestWithAuth, HttpResponse.BodyHandlers.ofString());
        }

        return response;
    }

    /**
     * Adds the Authorization header with the access token to the request.
     *
     * @param theRequest The request to add the Authorization header to.
     * @return The request with the Authorization header.
     */
    private HttpRequest addAuthorization(@Nonnull final HttpRequest theRequest) throws Exception {
        final HttpRequest.Builder builder = HttpRequest.newBuilder(theRequest.uri())
                .method(theRequest.method(), theRequest.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()));

        // Copy all headers from the original request.
        theRequest.headers().map().forEach((key, value) -> {
                builder.header(key, value.get(0));
        });

        // Add the Authorization header with the access token.
        builder.header("Authorization", "Bearer " + tokenManager.getToken().getAccessToken());
        return builder.build();
    }

}
