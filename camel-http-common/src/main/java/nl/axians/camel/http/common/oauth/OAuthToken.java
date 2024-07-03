package nl.axians.camel.http.common.oauth;

import jakarta.annotation.Nonnull;

import java.time.Instant;

/**
 * Represents an OAuth token.
 */
public class OAuthToken implements Token {

    private final String accessToken;
    private final Instant expiresAt;

    /**
     * Creates a new OAuth token.
     *
     * @param theAccessToken The access token.
     * @param theExpiresAt The expiration date and time of the token.
     */
    public OAuthToken(
            @Nonnull final String theAccessToken,
            @Nonnull final Instant theExpiresAt) {
        accessToken = theAccessToken;
        expiresAt = theExpiresAt;
    }

    /**
     * Gets the access token.
     *
     * @return The access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Check whether the token is expired.
     *
     * @return Whether the token is expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

}
