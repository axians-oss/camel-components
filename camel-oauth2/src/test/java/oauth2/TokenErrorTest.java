package oauth2;

import nl.axians.camel.oauth2.OAuth2Endpoint;
import nl.axians.camel.oauth2.OAuth2Exception;
import org.junit.jupiter.api.Test;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.Parameter.param;
import static org.mockserver.model.ParameterBody.params;

/**
 * Test that {@link OAuth2Endpoint} handles token errors with throwing a {@link OAuth2Exception}.
 */
public class TokenErrorTest extends BaseOAuth2Test {

    @Test
    public void Should_Throw_Error() {
        // Arrange
        HttpRequest request = request().withMethod("POST").withPath("/token");
        mockServer.when(request, Times.exactly(1))
                .respond(response().withStatusCode(200)
                        .withBody("{\"access_token\":\"1234567890\",\"token_type\":\"Bearer\",\"expires_in\":5}"));
        mockServer.when(request, Times.exactly(1))
                .respond(response().withStatusCode(401)
                        .withBody("{\"error_code\":\"access_denied\""));

        // Act
        try {
            template.sendBody("direct:start", null);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(OAuth2Exception.class);
        }

        // Assert
        mockServer.verify(request, VerificationTimes.exactly(1));
        mockServer.verify(request().withHeader("Authorization", "Basic " + "Y2xpZW50OnNlY3JldA=="));
        mockServer.verify(request().withBody(params(param("scope", "scope"),
                param("grant_type", "client_credentials"))));
        mockServer.verify(request().withHeader("Content-Type", "application/x-www-form-urlencoded"));
    }


}
