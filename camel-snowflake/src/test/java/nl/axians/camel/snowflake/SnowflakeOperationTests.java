package nl.axians.camel.snowflake;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for testing the different operations supported by {@link SnowflakeComponent}.
 */
public class SnowflakeOperationTests extends CamelTestSupport {

    @EndpointInject("direct:submit")
    private ProducerTemplate submitEndpoint;

    @EndpointInject("direct:status")
    private ProducerTemplate statusEndpoint;

    @EndpointInject("direct:cancel")
    private ProducerTemplate cancelEndpoint;

    @EndpointInject("mock:result")
    private MockEndpoint resultEndpoint;

    @Test
    public void ShouldSubmitStatements() throws Exception {
        try (final ClientAndServer mockServer = ClientAndServer.startClientAndServer(1080)) {
            // Given
            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token")
                            .withBody("grant_type=client_credentials&client_id=client_id&client_secret=client_secret&scope=scope"))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody("{\"access_token\":\"access_token\",\"token_type\":\"bearer\",\"expires_in\":3600}"));

            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements"))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody(""));

            // When
            submitEndpoint.sendBody("SELECT * FROM Customer");

            // Then
            resultEndpoint.expectedMessageCount(1);
            resultEndpoint.assertIsSatisfied();
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token"),
                    VerificationTimes.exactly(1)
            );
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements")
                            .withBody(getExpectedBody()),
                    VerificationTimes.exactly(1)
            );
        }
    }

    @Test
    public void ShouldCheckStatementStatus() throws Exception {
        try (final ClientAndServer mockServer = ClientAndServer.startClientAndServer(1080)) {
            // Given
            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token")
                            .withBody("grant_type=client_credentials&client_id=client_id&client_secret=client_secret&scope=scope"))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody("{\"access_token\":\"access_token\",\"token_type\":\"bearer\",\"expires_in\":3600}"));

            mockServer.when(HttpRequest.request()
                            .withMethod("GET")
                            .withPath("/api/v2/statements/1234567890"))
                    .respond(HttpResponse.response()
                            .withStatusCode(202)
                            .withBody("{\"statementHandle\":\"e4ce975e-f7ff-4b5e-b15e-bf25f59371ae\"}"));

            // When
            statusEndpoint.sendBodyAndHeader("", Snowflake.SNOWFLAKE_STATEMENT_HANDLE,
                    "1234567890");

            // Then
            resultEndpoint.expectedMessageCount(1);
            resultEndpoint.assertIsSatisfied();
            assertThat(resultEndpoint.getExchanges().get(0).getIn().getBody(String.class)).isEqualTo("{\"statementHandle\":\"e4ce975e-f7ff-4b5e-b15e-bf25f59371ae\"}");
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token"),
                    VerificationTimes.exactly(1)
            );
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("GET")
                            .withPath("/api/v2/statements/1234567890")
                            .withBody(""),
                    VerificationTimes.exactly(1)
            );
        }
    }

    @Test
    public void ShouldCancelStatement() throws Exception {
        try (final ClientAndServer mockServer = ClientAndServer.startClientAndServer(1080)) {
            // Given
            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token")
                            .withBody("grant_type=client_credentials&client_id=client_id&client_secret=client_secret&scope=scope"))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody("{\"access_token\":\"access_token\",\"token_type\":\"bearer\",\"expires_in\":3600}"));

            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements/1234567890/cancel"))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody(""));

            // When
            cancelEndpoint.sendBodyAndHeader("", Snowflake.SNOWFLAKE_STATEMENT_HANDLE,
                    "1234567890");

            // Then
            resultEndpoint.expectedMessageCount(1);
            resultEndpoint.assertIsSatisfied();
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token"),
                    VerificationTimes.exactly(1)
            );
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements/1234567890/cancel")
                            .withBody(""),
                    VerificationTimes.exactly(1)
            );
        }
    }

    @Test
    public void ShouldThrowExceptionWhenStatementHandleHeaderIsMissing() {
        try (final ClientAndServer mockServer = ClientAndServer.startClientAndServer(1080)) {
            // Given
            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token")
                            .withBody("grant_type=client_credentials&client_id=client_id&client_secret=client_secret&scope=scope"))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody("{\"access_token\":\"access_token\",\"token_type\":\"bearer\",\"expires_in\":3600}"));

            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements/1234567890/cancel"))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody(""));

            // When
            assertThatThrownBy(() -> statusEndpoint.sendBody("")).hasCauseInstanceOf(SnowflakeException.class)
                    .cause()
                    .hasMessage("SnowflakeStatementHandle header is missing or empty: Statement handle is required for operation: CheckStatementStatus");

            assertThatThrownBy(() -> cancelEndpoint.sendBody("")).hasCauseInstanceOf(SnowflakeException.class)
                    .cause()
                    .hasMessage("SnowflakeStatementHandle header is missing or empty: Statement handle is required for operation: CancelStatement");
        }
    }

    /**
     * Returns the expected body for a request to submit statements.
     *
     * @return The expected body.
     */
    private String getExpectedBody() {
        return String.format(SnowflakeProducer.REQUEST_BODY,
                "warehouse",
                "database",
                "schema",
                "SELECT * FROM Customer",
                "role");
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() {
                buildSubmitStatementsRoute();
                buildCheckStatementStatusRoute();
                buildCancelStatementRoute();
            }

            /**
             * Builds a route that submits statements to Snowflake.
             */
            private void buildSubmitStatementsRoute() {
                // @formatter:off
                from("direct:submit")
                    .to(Snowflake.uri(SnowflakeOperation.SubmitStatement)
                        .warehouse("warehouse")
                        .database("database")
                        .schema("schema")
                        .role("role")
                        .baseUrl("http://localhost:1080/api/v2")
                        .tokenUrl("http://localhost:1080/oauth/token")
                        .clientId("client_id")
                        .clientSecret("client_secret")
                        .scope("scope")
                        .build())
                    .to("mock:result")
                .end();
                // @formatter:on
            }

            /**
             * Builds a route that checks the status of a statement in Snowflake.
             */
            private void buildCheckStatementStatusRoute() {
                // @formatter:off
                from("direct:status")
                    .to(Snowflake.uri(SnowflakeOperation.CheckStatementStatus)
                        .warehouse("warehouse")
                        .database("database")
                        .schema("schema")
                        .role("role")
                        .baseUrl("http://localhost:1080/api/v2")
                        .tokenUrl("http://localhost:1080/oauth/token")
                        .clientId("client_id")
                        .clientSecret("client_secret")
                        .scope("scope")
                        .build())
                    .to("mock:result")
                .end();
                // @formatter:on
            }

            private void buildCancelStatementRoute() {
                // @formatter:off
                from("direct:cancel")
                    .to(Snowflake.uri(SnowflakeOperation.CancelStatement)
                        .warehouse("warehouse")
                        .database("database")
                        .schema("schema")
                        .role("role")
                        .baseUrl("http://localhost:1080/api/v2")
                        .tokenUrl("http://localhost:1080/oauth/token")
                        .clientId("client_id")
                        .clientSecret("client_secret")
                        .scope("scope")
                        .build())
                    .to("mock:result")
                .end();
                // @formatter:on
            }

        };
    }


}
