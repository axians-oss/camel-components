package nl.axians.camel.snowflake.springboot;

import jakarta.annotation.Nonnull;
import nl.axians.camel.snowflake.SnowflakeComponent;
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

/**
 * The {@link SnowflakeComponent} auto-configuration.
 */
@Configuration(proxyBeanMethods = false)
@Conditional(ConditionalOnCamelContextAndAutoConfigurationBeans.class)
@EnableConfigurationProperties({ComponentConfigurationProperties.class, SnowflakeComponentConfiguration.class})
@ConditionalOnHierarchicalProperties({"camel.component", "camel.component.snowflake"})
@AutoConfigureAfter({CamelAutoConfiguration.class})
public class SnowflakeComponentAutoConfiguration {

    private final ApplicationContext applicationContext;
    private final CamelContext camelContext;
    private final SnowflakeComponentConfiguration configuration;

    /**
     * Create a new instance.
     *
     * @param theApplicationContext The application context
     * @param theCamelContext The camel context
     * @param theConfiguration The configuration
     */
    public SnowflakeComponentAutoConfiguration(
            @Nonnull final ApplicationContext theApplicationContext,
            @Nonnull final CamelContext theCamelContext,
            @Nonnull final SnowflakeComponentConfiguration theConfiguration) {
        applicationContext = theApplicationContext;
        camelContext = theCamelContext;
        configuration = theConfiguration;
    }

    /**
     * Configure the {@link SnowflakeComponent}.
     *
     * @return The {@link ComponentCustomizer}
     */
    @Lazy
    @Bean
    public ComponentCustomizer configureSnowflakeComponent() {
        return new ComponentCustomizer() {

            @Override
            public void configure(final String theName, final Component theTarget) {
                CamelPropertiesHelper.copyProperties(camelContext, configuration, theTarget);
            }

            @Override
            public boolean isEnabled(final String theName, final Component theTarget) {
                return HierarchicalPropertiesEvaluator.evaluate(applicationContext,
                        "camel.component.customizer", "camel.component.snowflake.customizer")
                        && theTarget instanceof SnowflakeComponent;
            }

        };
    }

}
