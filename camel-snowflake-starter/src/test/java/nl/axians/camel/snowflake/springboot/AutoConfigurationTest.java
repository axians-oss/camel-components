package nl.axians.camel.snowflake.springboot;

import nl.axians.camel.snowflake.Snowflake;
import nl.axians.camel.snowflake.SnowflakeComponent;
import nl.axians.camel.snowflake.SnowflakeOperation;
import org.apache.camel.Configuration;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

/**
 * Test if the {@link SnowflakeComponent} is correctly loaded by the Spring Boot
 * autoconfiguration.
 */
@CamelSpringBootTest
@EnableAutoConfiguration
@SpringBootTest(classes=AutoConfigurationTest.Config.class)
public class AutoConfigurationTest {

    @EndpointInject("direct:start")
    private ProducerTemplate submitEndpoint;

    @EndpointInject("mock:result")
    private MockEndpoint resultEndpoint;

    @Configuration
    static class Config {

        @Bean
        RoutesBuilder routesBuilder() {
            return new RouteBuilder() {

                @Override
                public void configure() {
                    // @formatter:off
                    from("direct:start")
                        .setBody(constant("SELECT * FROM CUSTOMER"))
                        .to(Snowflake.uri(SnowflakeOperation.SubmitStatement).build())
                        .to("mock:result")
                    .end();
                    // @formatter:on
                }

            };
        }

    }

    @Test
    public void ShouldAutowireSnowflakeComponent() throws InterruptedException {
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
                            .withPath("/api/v2/statements"),
                    VerificationTimes.exactly(1)
            );
        }
    }

}

