package io.adopteunops.etl.service.processor;

import io.adopteunops.etl.config.ESBufferConfiguration;
import io.adopteunops.etl.config.ESConfiguration;
import io.adopteunops.etl.domain.ESBuffer;
import io.adopteunops.etl.service.ESErrorRetryWriter;
import lombok.AllArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@AllArgsConstructor
public class ProcessorBeanFactory {
    private final ESErrorRetryWriter esErrorRetryWriter;
    private final RestHighLevelClient client;
    private final ESConfiguration esConfiguration;
    private final ESBufferConfiguration esBufferConfiguration;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ValidateDataToElasticSearchProcessor elasticSearchProcessor() {
        ESBuffer esBuffer = new ESBuffer(client,esBufferConfiguration, esConfiguration);
        return new ValidateDataToElasticSearchProcessor(esBuffer,esErrorRetryWriter);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public LoggingProcessor loggingProcessor() {
        return new LoggingProcessor();
    }
}
