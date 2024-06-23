package nl.axians.camel.snowflake;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

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
    }

    @Test
    public void shouldFetchTokenWhenExpired() throws Exception {
    }

    @Test
    public void shouldFetchTokenWhenUnauthorized() throws Exception {
    }

    @Test
    public void shouldThrowExceptionWhenTokenCannotBeFetched() throws Exception {
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                // @formatter:off
                from("direct:start")
                    .to(Snowflake.uri()
                        .operation(SnowflakeOperation.SubmitStatements)
                        .build())
                    .to("mock:result")
                .end();
                // @formatter:on
            }

        };
    }

}
