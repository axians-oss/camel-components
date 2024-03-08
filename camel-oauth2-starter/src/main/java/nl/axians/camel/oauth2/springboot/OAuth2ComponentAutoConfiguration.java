package nl.axians.camel.oauth2.springboot;

import nl.axians.camel.oauth2.OAuth2Component;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.spi.ComponentCustomizer;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.apache.camel.spring.boot.ComponentConfigurationProperties;
import org.apache.camel.spring.boot.util.CamelPropertiesHelper;
import org.apache.camel.spring.boot.util.ConditionalOnCamelContextAndAutoConfigurationBeans;
import org.apache.camel.spring.boot.util.ConditionalOnHierarchicalProperties;
import org.apache.camel.spring.boot.util.HierarchicalPropertiesEvaluator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration(proxyBeanMethods = false)
@Conditional(ConditionalOnCamelContextAndAutoConfigurationBeans.class)
@EnableConfigurationProperties({ComponentConfigurationProperties.class, OAuth2ComponentConfiguration.class})
@ConditionalOnHierarchicalProperties({"camel.component", "camel.component.oauth2"})
@AutoConfigureAfter({CamelAutoConfiguration.class})
public class OAuth2ComponentAutoConfiguration {

    private final ApplicationContext applicationContext;
    private final CamelContext camelContext;
    private final OAuth2ComponentConfiguration configuration;

    public OAuth2ComponentAutoConfiguration(final ApplicationContext theApplicationContext,
                                    final CamelContext theCamelContext,
                                    final OAuth2ComponentConfiguration theConfiguration) {
        applicationContext = theApplicationContext;
        camelContext = theCamelContext;
        configuration = theConfiguration;
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureOAuth2Component() {

        return new ComponentCustomizer() {

            @Override
            public void configure(final String theName, final Component theTarget) {
                CamelPropertiesHelper.copyProperties(camelContext, configuration, theTarget);
            }

            @Override
            public boolean isEnabled(final String theName, final Component theTarget) {
                return HierarchicalPropertiesEvaluator.evaluate(
                        applicationContext,
                        "camel.component.customizer",
                        "camel.component.oauth2.customizer")
                        && theTarget instanceof OAuth2Component;
            }

        };
    }

}
