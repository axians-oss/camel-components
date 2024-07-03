package nl.axians.camel.http.common.oauth;

import java.io.IOException;

/**
 * Manages tokens for OAuth authentication.
 */
public interface TokenManager {

    /**
     * Get the current token. Will fetch a new token if the current token has not been set or is expired.
     *
     * @return The OAuth token.
     * @throws IOException          If an error occurs while fetching the token.
     * @throws InterruptedException If the request is interrupted while fetching the token.
     */
    Token getToken() throws IOException, InterruptedException;

    /**
     * Fetch a new OAuth token.
     *
     * @throws IOException          If an error occurs while fetching the token.
     * @throws InterruptedException If the request is interrupted while fetching the token.
     */
    void fetchToken() throws IOException, InterruptedException;


}
