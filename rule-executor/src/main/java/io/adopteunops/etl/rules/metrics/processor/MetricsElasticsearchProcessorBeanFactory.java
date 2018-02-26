package io.adopteunops.etl.rules.metrics.processor;

import io.adopteunops.etl.config.ESBufferConfiguration;
import io.adopteunops.etl.config.ESConfiguration;
import io.adopteunops.etl.domain.ESBuffer;
import io.adopteunops.etl.domain.RetentionLevel;
import io.adopteunops.etl.service.ESErrorRetryWriter;
import lombok.AllArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@AllArgsConstructor
public class MetricsElasticsearchProcessorBeanFactory {

    private final ESErrorRetryWriter esErrorRetryWriter;
    private final RestHighLevelClient client;
    private final ESConfiguration esConfiguration;
    private final ESBufferConfiguration esBufferConfiguration;
    private final JavaMailSender javaMailSender;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MetricsElasticsearchProcessor metricsElasticsearchProcessor(RetentionLevel retentionLevel) {
        ESBuffer esBuffer = new ESBuffer(client, esBufferConfiguration, esConfiguration);
        return new MetricsElasticsearchProcessor(esBuffer, esErrorRetryWriter, retentionLevel);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MetricsEmailProcessor emailProcessor(String destinationEmail) {
        return new MetricsEmailProcessor(javaMailSender, destinationEmail);
    }
}
