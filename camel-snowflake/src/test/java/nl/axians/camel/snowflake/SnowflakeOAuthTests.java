package nl.axians.camel.snowflake;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for testing the OAuth2 support in the {@link SnowflakeComponent}.
 */
public class SnowflakeOAuthTests extends CamelTestSupport {

    @EndpointInject("direct:start")
    private ProducerTemplate startEndpoint;

    @EndpointInject("mock:result")
    private MockEndpoint resultEndpoint;

    @Test
    public void shouldFetchTokenFirstTime() throws Exception {
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
            startEndpoint.sendBody("SELECT * FROM Customer");

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
                            .withPath("/api/v2/statements"),
                    VerificationTimes.exactly(1)
            );
        }
    }

    @Test
    public void shouldUseCachedToken() throws Exception {
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
                    .respond(org.mockserver.model.HttpResponse.response()
                            .withStatusCode(200)
                            .withBody(""));

            // When
            startEndpoint.sendBody("SELECT * FROM Customer");   // Wil set the token.
            startEndpoint.sendBody("SELECT * FROM Customer");

            // Then
            resultEndpoint.expectedMessageCount(2);
            resultEndpoint.assertIsSatisfied();
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token"),
                    VerificationTimes.exactly(1)    // Only first time should fetch token.
            );
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements"),
                    VerificationTimes.exactly(2)    // Once for each call.
            );
        }

    }

    @Test
    public void shouldFetchTokenWhenExpired() throws Exception {
        try (final ClientAndServer mockServer = ClientAndServer.startClientAndServer(1080)) {
            // Given
            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token")
                            .withBody("grant_type=client_credentials&client_id=client_id&client_secret=client_secret&scope=scope"))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody("{\"access_token\":\"access_token\",\"token_type\":\"bearer\",\"expires_in\":10}"));

            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements"))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody(""));

            // When
            startEndpoint.sendBody("SELECT * FROM Customer");   // Wil set the token that expires in 10 seconds.
            Thread.sleep(Duration.ofSeconds(15).toMillis());
            startEndpoint.sendBody("SELECT * FROM Customer");

            // Then
            resultEndpoint.expectedMessageCount(2);
            resultEndpoint.assertIsSatisfied();
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token"),
                    VerificationTimes.exactly(2)    // Once first time and once when expired.
            );
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements"),
                    VerificationTimes.exactly(2)    // Once first time and once when expired.
            );
        }
    }

    @Test
    public void shouldFetchTokenWhenUnauthorized() throws Exception {
        try (final ClientAndServer mockServer = ClientAndServer.startClientAndServer(1080)) {
            // Given
            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token")
                            .withBody("grant_type=client_credentials&client_id=client_id&client_secret=client_secret&scope=scope"),
                            Times.once())
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody("{\"access_token\":\"access_token\",\"token_type\":\"bearer\",\"expires_in\":3600}"));

            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements"),
                            Times.once())
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody(""));

            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements"),
                            Times.once())
                    .respond(HttpResponse.response()
                            .withStatusCode(401)
                            .withBody(""));

            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token")
                            .withBody("grant_type=client_credentials&client_id=client_id&client_secret=client_secret&scope=scope"),
                            Times.once())
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody("{\"access_token\":\"access_token\",\"token_type\":\"bearer\",\"expires_in\":3600}"));

            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements"),
                            Times.once())
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withBody(""));

            // When
            startEndpoint.sendBody("SELECT * FROM Customer");
            startEndpoint.sendBody("SELECT * FROM Customer");

            // Then
            resultEndpoint.expectedMessageCount(2);
            resultEndpoint.assertIsSatisfied();
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token"),
                    VerificationTimes.exactly(2)    // Once first time and once when not authorized.
            );
            mockServer.verify(
                    HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements"),
                    VerificationTimes.exactly(3)    // Once first time, once when not authorized and once on retry.
            );
        }
    }

    @Test
    public void shouldThrowExceptionWhenTokenCannotBeFetched() throws Exception {
        try (final ClientAndServer mockServer = ClientAndServer.startClientAndServer(1080)) {
            // Given
            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/oauth/token")
                            .withBody("grant_type=client_credentials&client_id=client_id&client_secret=client_secret&scope=scope"))
                    .respond(HttpResponse.response()
                            .withStatusCode(403)
                            .withBody("403 Forbidden"));

            // First time we return 200.
            mockServer.when(HttpRequest.request()
                            .withMethod("POST")
                            .withPath("/api/v2/statements"))
                    .respond(org.mockserver.model.HttpResponse.response()
                            .withStatusCode(200)
                            .withBody(""));

            // When
            assertThatThrownBy(() -> startEndpoint.sendBody("SELECT * FROM Customer"))
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(IOException.class)
                    .cause()
                        .hasMessage("Failed to fetch OAuth token: 403 Forbidden");

            // Then
            resultEndpoint.expectedMessageCount(0);
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
                            .withPath("/api/v2/statements"),
                    VerificationTimes.exactly(0)
            );
        }

    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() {
                // @formatter:off
                from("direct:start")
                    .to(Snowflake.uri()
                        .operation(SnowflakeOperation.SubmitStatement)
                        .tokenUrl("http://localhost:1080/oauth/token")
                        .clientId("client_id")
                        .clientSecret("client_secret")
                        .scope("scope")
                        .baseUrl("http://localhost:1080/api/v2")
                        .database("database")
                        .schema("schema")
                        .warehouse("warehouse")
                        .role("role")
                        .build())
                    .to("mock:result")
                .end();
                // @formatter:on
            }

        };
    }

}
