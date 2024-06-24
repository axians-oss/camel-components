# Camel Snowflake Component Spring Boot Starter

This is the Spring Boot starter for the Camel Snowflake component that can be used to access Snowflake using the REST API.

## Usage

The URI for the component is `snowflake:operation`. The operation can be one of the following:

- __SubmitStatement__. To submit one or more SQL statements.
- __CheckStatementStatus__. To check the status of a submitted statement.
- __CancelStatement__. To cancel a running statement.

Other parameters you can set on the component and/or endpoint are:

| Name           | Default | Description                                                                                          |
|----------------|---------|------------------------------------------------------------------------------------------------------|
| `clientId`     |         | The client id for the OAuth2 server.                                                                 |
| `clientSecret` |         | The client secret for the OAuth2 server.                                                             |
| `tokenUrl`     |         | The URL to the token endpoint.                                                                       |
| `scope`        |         | The scope to use when retrieving the token.                                                          |
| `baseUrl`      |         | The Snowflake RESt API url. for mat is `https://<account_identifier>.snowflakecomputing.com/api/v2`. |
| `warehouse`    |         | The Snowflake warehouse to connect to.                                                               |
| `database`     |         | The Snowflake database to connect to.                                                                |
| `schema`       |         | The Snowflake database schema to use.                                                                |
| `role`         |         | The Snowflake role to use.                                                                           |

When using the `CheckStatementStatus` and `CancelStatement` operations, you need to set the `Snowflake.SNOWFLAKE_STATEMENT_HANDLE` header to the statement handle you want to check or cancel. You can use the `Snowflake.uri()` and `Snowflake.uri(operation)` methods to build the URI for the endpoint.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE.md) file for details.


