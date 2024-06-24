package nl.axians.camel.snowflake;

import jakarta.annotation.Nonnull;

/**
 * Snowflake component specific exception.
 */
public class SnowflakeException extends RuntimeException {

    /**
     * Creates a new Snowflake exception with the given message.
     *
     * @param theMessage The message of the exception.
     */
    public SnowflakeException(@Nonnull final String theMessage) {
        super(theMessage);
    }

}
