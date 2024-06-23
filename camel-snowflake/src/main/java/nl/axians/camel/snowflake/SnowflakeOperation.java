package nl.axians.camel.snowflake;

/**
 * Enum to define the different operations that can be performed on the Snowflake API.
 */
public enum SnowflakeOperation {
    /**
     * Operation to submit one or more SQL statements to Snowflake.
     */
    SubmitStatements,
    /**
     * Operation to check the status of a SQL statement that was submitted to Snowflake asynchronously.
     */
    CheckStatementStatus,
    /**
     * Operation to cancel a SQL statement that was submitted to Snowflake asynchronously.
     */
    CancelStatement
}
