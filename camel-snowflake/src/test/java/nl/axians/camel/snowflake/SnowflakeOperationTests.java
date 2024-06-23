package nl.axians.camel.snowflake;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

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

    @Test
    public void ShouldSubmitStatements() throws Exception {
    }

    @Test
    public void ShouldCheckStatementStatus() throws Exception {
    }

    @Test
    public void ShouldCancelStatement() throws Exception {
    }

    @Test
    public void ShouldThrowExceptionWhenStatementHandleHeaderIsMissing() throws Exception {
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
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
                    .to(Snowflake.uri()
                        .operation(SnowflakeOperation.SubmitStatements)
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
                    .to(Snowflake.uri()
                        .operation(SnowflakeOperation.CheckStatementStatus)
                        .build())
                    .to("mock:result")
                .end();
                // @formatter:on
            }

            private void buildCancelStatementRoute() {
                // @formatter:off
                from("direct:cancel")
                    .to(Snowflake.uri()
                        .operation(SnowflakeOperation.CancelStatement)
                        .build())
                    .to("mock:result")
                .end();
                // @formatter:on
            }

        };
    }


}
