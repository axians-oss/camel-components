package nl.axians.camel.language.datasonnet;

import com.datasonnet.document.MediaTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.stream.InputStreamCache;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static nl.axians.camel.language.datasonnet.DatasonnetBuilderSupport.dsonnet;
import static nl.axians.camel.language.datasonnet.DatasonnetBuilderSupport.dsonnetVarName;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MappingTests extends CamelTestSupport {

    @Test
    public void shouldUseBodyAndVariables() throws Exception {
        // Arrange
        final String body = loadResource("/simple-mapping-payload.json");
        final String expectedResult = loadResource("/simple-mapping-result.json");

        // Act
        template.sendBody("direct:transform", body);

        // Assert
        final MockEndpoint mockResult = getMockEndpoint("mock:result");
        mockResult.expectedMessageCount(1);

        final Exchange exchange = mockResult.assertExchangeReceived(0);
        final Object result = exchange.getMessage().getBody();

        assertThat(result).isInstanceOf(String.class);
        JSONAssert.assertEquals(expectedResult, (String) result, false);
    }

    @Test
    public void shouldUseStreamCacheBodyAndVariables() throws Exception {
        // Arrange
        final String body = loadResource("/simple-mapping-payload.json");
        final InputStreamCache streamCache = new InputStreamCache(body.getBytes(StandardCharsets.UTF_8));
        final String expectedResult = loadResource("/simple-mapping-result.json");

        // Act
        template.sendBody("direct:transform", streamCache);

        // Assert
        final MockEndpoint mockResult = getMockEndpoint("mock:result");
        mockResult.expectedMessageCount(1);

        final Exchange exchange = mockResult.assertExchangeReceived(0);
        final Object result = exchange.getMessage().getBody();

        assertThat(result).isInstanceOf(String.class);
        JSONAssert.assertEquals(expectedResult, (String) result, false);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                final String property = loadResource("/simple-mapping-property.json");
                final String header = loadResource("/simple-mapping-header.json");

                // @formatter:off
                from("direct:transform")
                    .setProperty(dsonnetVarName("property", MediaTypes.APPLICATION_JSON), constant(property))
                    .setHeader(dsonnetVarName("header", MediaTypes.APPLICATION_JSON), constant(header))
                    .setProperty(dsonnetVarName("email", MediaTypes.APPLICATION_JAVA), constant("john.doe@heaven.com"))
                    .setProperty(dsonnetVarName("name", MediaTypes.APPLICATION_JAVA), jsonpath("$.name", String.class))
                    .transform(dsonnet("resource:classpath:/simple-mapping.ds", String.class,
                            "property", "header", "email", "name"))
                    .to("mock:result");
                // @formatter:on
            }

        };
    }

    /**
     * Load a resource from the classpath.
     *
     * @param theResource The resource to load.
     * @return The content of the resource.
     * @throws IOException If an error occurs while reading the resource.
     */
    private String loadResource(final String theResource) throws IOException {
        try (final InputStream is = getClass().getResourceAsStream(theResource)) {
            assert is != null;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
