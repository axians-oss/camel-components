package nl.axians.camel.datasonnet.springboot;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * The Auto-configuration class for the DataSonnet type converters.
 */
@AutoConfiguration
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
