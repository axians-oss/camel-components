package nl.axians.camel.datasonnet.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Auto-configuration class for the DataSonnet type converters.
 */
@Configuration(proxyBeanMethods = false)
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

}
