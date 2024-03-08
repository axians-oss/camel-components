package nl.axians.camel.oauth2;

import java.text.MessageFormat;

/**
 * Exception thrown when an OAuth2 error occurs.
 */
public class OAuth2Exception extends RuntimeException {

    /**
     * Create a new OAuth2 exception.
     *
     * @param format The format string.
     * @param args   The format arguments.
     */
    public OAuth2Exception(String format, Object... args) {
        super(MessageFormat.format(format, args));
    }

}
