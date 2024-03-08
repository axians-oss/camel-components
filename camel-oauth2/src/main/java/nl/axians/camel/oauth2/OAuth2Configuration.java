package nl.axians.camel.oauth2;

import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;
import org.apache.camel.spi.UriPath;

import java.util.Base64;

@SuppressWarnings({"LombokSetterMayBeUsed", "LombokGetterMayBeUsed"})
@UriParams
public class OAuth2Configuration implements Cloneable {

    @UriPath
    @Metadata(required = true)
    private String name;

    @UriParam(label = "security", secret = true)
    @Metadata(required = true)
    private String clientId;

    @UriParam(label = "security", secret = true)
    @Metadata(required = true)
    private String clientSecret;

    @UriParam(label = "common")
    @Metadata(required = true)
    private String scope;

    @UriParam(label = "common")
    @Metadata(required = true)
    private String accessTokenUrl;

    @UriParam(label = "common", defaultValue = "300")
    private long tokenExpirationThreshold = 300;

    @UriParam(label = "common")
    private String redirectURI;

    /**
     * The name of the access token. This is a user provided name and should be unique for each OAuth2
     * access token urt.
     *
     * @return The name of the access token.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the OAuth2 token name.
     *
     * @param name The name of the OAuth2 token. This is a user provided name and should be unique for each
     *             OAuth2 access token url.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the OAuth2 client id.
     *
     * @return The OAuth2 client id.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Set the OAuth2 client id.
     *
     * @param clientId The OAuth2 client id.
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Get the OAuth2 client secret.
     *
     * @return The OAuth2 client secret.
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Set the OAuth2 client secret.
     *
     * @param clientSecret The OAuth2 client secret.
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Get the OAuth2 access token url. This is the endpoint where to retrieve the access token.
     *
     * @return The OAuth2 access token url.
     */
    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    /**
     * Get the token expiration threshold. This is the amount of time in seconds before the actual token expires.
     * The default value is 5 minutes (300 seconds).
     *
     * @return The token expiration threshold in seconds.
     */
    public long getTokenExpirationThreshold() {
        return tokenExpirationThreshold;
    }


    /**
     * Set the token expiration threshold. This is the amount of time in seconds before the actual token expires.
     *
     * @param tokenExpirationThreshold The token expiration threshold in seconds.
     */
    public void setTokenExpirationThreshold(long tokenExpirationThreshold) {
        this.tokenExpirationThreshold = tokenExpirationThreshold;
    }

    /**
     * Get the OAuth2 scope.
     *
     * @return  The OAuth2 scope.
     */
    public String getScope() {
        return scope;
    }

    /**
     * Set the OAuth2 scope.
     *
     * @param scope The OAuth2 scope.
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Get the OAuth2 redirect URI.
     *
     * @return The OAuth2 redirect URI.
     */
    public String getRedirectURI() {
        return redirectURI;
    }

    /**
     * Set the OAuth2 redirect URI.
     *
     * @param redirectURI The OAuth2 redirect URI.
     */
    public void setRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
    }

    /**
     * Set the OAuth2 access token url.
     *
     * @param accessTokenUrl The OAuth2 access token url.
     */
    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

    public String getAuthorizationHeader() {
        final String value = clientId + ":" + clientSecret;
        return "Basic " + Base64.getEncoder().encodeToString(value.getBytes());
    }

    public OAuth2Configuration copy() {
        try {
            return  (OAuth2Configuration) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
