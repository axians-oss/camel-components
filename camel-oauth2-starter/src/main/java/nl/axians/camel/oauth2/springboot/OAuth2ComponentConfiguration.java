package nl.axians.camel.oauth2.springboot;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.spring.boot.ComponentConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "camel.component.oauth2")
public class OAuth2ComponentConfiguration extends ComponentConfigurationProperties {

    /**
     * The name of the token.
     */
    private String name;

    /**
     * The client id.
     */
    private String clientId;

    /**
     * The client secret.
     */
    private String clientSecret;

    /**
     * The scope.
     */
    private String scope;

    /**
     * The access token url.
     */
    private String accessTokenUrl;

    /**
     * The token expiration threshold in seconds. The time to live of the token will be reduced by this amount of
     * seconds. The default is 300 seconds (5 minutes).
     */
    private long tokenExpirationThreshold = 300;

    /**
     * The redirect URI.
     */
    private String redirectURI;

}
