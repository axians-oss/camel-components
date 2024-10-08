package nl.axians.camel.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class OAuth2Producer extends DefaultProducer {

    private final OAuth2Endpoint endpoint;
    private final ObjectMapper objectMapper;
    private final OAuth2Configuration configuration;
    private HttpClient httpClient;
    private HttpRequest httpRequest;
    private String accessToken;
    private String tokenType;
    private Instant tokenExpiration;

    /**
     * Create a new OAuth2 producer.
     *
     * @param endpoint The {@link OAuth2Endpoint} that created this instance.
     * @throws URISyntaxException If the access token URL is invalid.
     */
    public OAuth2Producer(final OAuth2Endpoint endpoint) throws URISyntaxException {
        super(endpoint);

        this.endpoint = endpoint;
        this.configuration = endpoint.getConfiguration();
        this.objectMapper = new ObjectMapper();
        initHttpClient();
    }

    /**
     * Initialize the HTTP client and request.
     *
     * @throws URISyntaxException If the URI is invalid.
     */
    private void initHttpClient() throws URISyntaxException {
        httpClient = HttpClient.newBuilder().build();

        // Create the access token request form data request parameters
        final Map<String, String> formData = new HashMap<>();
        formData.put("grant_type", configuration.getGrantType());
        if (configuration.getScope() != null) {
            formData.put("scope", configuration.getScope());
        }
        if (configuration.getRedirectURI() != null) {
            formData.put("redirect_uri", configuration.getRedirectURI());
        }
        if (configuration.getUsername() != null) {
            formData.put("username", configuration.getUsername());
        }
        if (configuration.getPassword() != null) {
            formData.put("password", configuration.getPassword());
        }

        if (!configuration.isUseBasicAuthorization()) {
            if (configuration.getClientId() != null) {
                formData.put("client_id", configuration.getClientId());
            }
            if (configuration.getClientSecret() != null) {
                formData.put("client_secret", configuration.getClientSecret());
            }
        }

        // Create the access token request
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(configuration.getAccessTokenUrl()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(formData)));

        // Add basic authorization header if configured.
        if (configuration.isUseBasicAuthorization()) {
            builder.header("Authorization", configuration.getAuthorizationHeader());
        }

        httpRequest = builder.build();
    }

    /**
     * Create a new access token request body.
     *
     * @param formData The form data to include in the request body.
     * @return The request body as a URL encoded string.
     */
    private String getFormDataAsString(Map<String, String> formData) {
        return formData.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                        URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (accessToken == null || tokenExpiration.isBefore(Instant.now())) {
            log.debug("Retrieving access token from {} for scope {}", httpRequest.uri().toString(), configuration.getScope());
            final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new OAuth2Exception("Failed to retrieve access token {0}: {1} {2}",
                        configuration.getName(), response.statusCode(), response.body());
            }

            final TokenResponse tokenResponse = objectMapper.readValue(response.body(), TokenResponse.class);
            long timeToLive = tokenResponse.getExpiresIn() - endpoint.getConfiguration().getTokenExpirationThreshold();
            accessToken = tokenResponse.getAccessToken();
            tokenType = tokenResponse.getTokenType();
            tokenExpiration = Instant.now().plusSeconds(timeToLive > 0 ? timeToLive : 0);
        }

        log.debug("Setting Authorization header with token type {} and expiration {}", tokenType,
                DateTimeFormatter.ISO_INSTANT.format(tokenExpiration));
        exchange.getIn().setHeader("Authorization", tokenType + " " + accessToken);
    }

}
