package nl.axians.camel.datasonnet.springboot;

import com.datasonnet.spi.Library;
import lombok.extern.slf4j.Slf4j;
import nl.axians.camel.language.datasonnet.DatasonnetLanguage;
import org.apache.camel.spi.Language;
import org.apache.camel.spi.LanguageCustomizer;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.apache.camel.spring.boot.util.ConditionalOnCamelContextAndAutoConfigurationBeans;
import org.apache.camel.spring.boot.util.HierarchicalPropertiesEvaluator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Optional;

/**
 * The Auto-configuration class for the DataSonnet type converters.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(CamelAutoConfiguration.class)
@Conditional(ConditionalOnCamelContextAndAutoConfigurationBeans.class)
@Slf4j
public class DataSonnetAutoConfiguration {

    /**
     * Create a new instance of the {@link }DataSonnetTypeConverters}.
     *
     * @return a new instance of the DataSonnetTypeConverters.
     */
    @Bean
    public DataSonnetTypeConverters dataSonnetConverter() {
        return new DataSonnetTypeConverters();
    }

    @Lazy
    @Bean
    public org.apache.camel.spi.LanguageCustomizer configureDatasonnetLanguage(
            final ApplicationContext applicationContext,
            final Optional<List<Library>> libraries) {

        return new LanguageCustomizer() {

            @Override
            public void configure(String name, Language target) {
                int size = libraries.map(List::size).orElse(0);
                log.info("Number of libraries found: {}", size);
                if (libraries.isPresent() && target instanceof DatasonnetLanguage)
                    ((DatasonnetLanguage) target).getLibraries().addAll(libraries.get());
            }

            @Override
            public boolean isEnabled(String name, Language target) {
                return HierarchicalPropertiesEvaluator.evaluate(
                        applicationContext,
                        "camel.language.customizer",
                        "camel.language.datasonnet.customizer")
                        && target instanceof DatasonnetLanguage;
            }
        };
    }

}
