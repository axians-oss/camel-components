package nl.axians.camel.http.common.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Instant;

/**
 * Manages OAuth tokens.
 */
public class OAuthTokenManager {

    public static final String OAUTH_REQUEST_BODY_TEMPLATE = "grant_type=client_credentials&" +
            "client_id={0}&client_secret={1}";

    private OAuthToken token;
    private final HttpClient httpClient;
    private final String tokenUrl;
    private final String requestBody;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new OAuth token manager.
     *
     * @param theHttpClient The HTTP client to use.
     * @param theTokenUrl The OAuth token URL.
     * @param theClientId The OAuth client ID.
     * @param theClientSecret The OAuth client secret.
     */
    public OAuthTokenManager(
            @Nonnull final HttpClient theHttpClient,
            @Nonnull final String theTokenUrl,
            @Nonnull final String theClientId,
            @Nonnull final String theClientSecret,
            final String theScope,
            final String tenantId) {
        httpClient = theHttpClient;
        tokenUrl = theTokenUrl;
        objectMapper = new ObjectMapper();
        String body = MessageFormat.format(OAUTH_REQUEST_BODY_TEMPLATE, theClientId, theClientSecret);

        // Add the scope if it is not null or blank.
        if (theScope != null && !theScope.isBlank()) {
            body += "&scope=" + theScope;
        }

        // Add the tenant ID if it is not null or blank.
        if (tenantId != null && !tenantId.isBlank()) {
            body += "&tenant_id=" + tenantId;
        }

        requestBody = body;
    }

    /**
     * Gets the OAuth token. Will fetch a new token if the current token has not been set or is expired.
     *
     * @return The OAuth token.
     * @throws IOException If an error occurs while fetching the token.
     * @throws InterruptedException If the thread is interrupted while fetching the token.
     */
    public OAuthToken getToken() throws IOException, InterruptedException {
        if (token == null || token.isExpired()) {
            fetchToken();
        }

        return token;
    }

    /**
     * Fetches a new OAuth token.
     *
     * @throws IOException If an error occurs while fetching the token.
     * @throws InterruptedException If the thread is interrupted while fetching the token.
     */
    public void fetchToken() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch OAuth token: " + response.body());
        }

        token = parseToken(response);
    }

    /**
     * Parses the token from the response. This default implementation expects the token to be in JSON format as
     * described in the OAuth 2.0 specification. If your token is in a different format, you can override this method.
     *
     * @param theResponse The response.
     * @return The OAuth token.
     * @throws JsonProcessingException If an error occurs while parsing the token.
     */
    protected OAuthToken parseToken(HttpResponse<String> theResponse) throws JsonProcessingException {
        final JsonNode jsonNode = objectMapper.readTree(theResponse.body());
        final String accessToken = jsonNode.get("access_token").asText();
        final long expiresIn = jsonNode.get("expires_in").asLong();
        final Instant expirationTime = Instant.now().plusSeconds(expiresIn);
        return new OAuthToken(accessToken, expirationTime);
    }

}
