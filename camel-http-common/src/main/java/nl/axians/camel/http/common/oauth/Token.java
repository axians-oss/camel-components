package nl.axians.camel.http.common.oauth;

/**
 * Represents a token.
 */
public interface Token {

    /**
     * Get the access token.
     *
     * @return The access token.
     */
    String getAccessToken();

    /**
     * Check whether the token is expired.
     *
     * @return Whether the token is expired.
     */
    boolean isExpired();

}
