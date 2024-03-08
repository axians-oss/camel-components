package oauth2;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.mockserver.integration.ClientAndServer;

/**
 * Base class for OAuth2 tests.
 */
public abstract class BaseOAuth2Test extends CamelTestSupport {

    protected ClientAndServer mockServer;
    protected int mockServerPort = 1080;

    @Override
    protected void doPreSetup() throws Exception {
        super.doPreSetup();
        mockServer = ClientAndServer.startClientAndServer(mockServerPort);
    }

    @Override
    protected void doPostTearDown() throws Exception {
        super.doPostTearDown();
        mockServer.stop();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() {
                from("direct:start")
                        .to("oauth2://test?clientId=client&clientSecret=secret&accessTokenUrl=http://localhost:1080/token&scope=scope")
                        .to("mock:result");
            }

        };
    }

}
