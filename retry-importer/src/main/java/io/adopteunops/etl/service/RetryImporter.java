package io.adopteunops.etl.service;

import io.adopteunops.etl.admin.KafkaAdminService;
import io.adopteunops.etl.config.ESBufferConfiguration;
import io.adopteunops.etl.config.ESConfiguration;
import io.adopteunops.etl.config.KafkaConfiguration;
import io.adopteunops.etl.domain.ESBuffer;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.serdes.ValidateDataDeserializer;
import io.adopteunops.etl.serdes.ValidateDataSerializer;
import io.adopteunops.etl.utils.KafkaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = false)
@Slf4j
public class RetryImporter {

    private static final String INPUT_PROCESS_RETRY = "es-retry";
    private final KafkaStreams retryStream;

    public RetryImporter(RestHighLevelClient esClient, ESConfiguration esConfiguration, ESBufferConfiguration esBufferConfiguration, ESErrorRetryWriter esErrorRetryWriter, KafkaConfiguration kafkaConfiguration, KafkaAdminService kafkaAdminService) {
        kafkaAdminService.buildTopic(kafkaConfiguration.getRetryTopic());

        StreamsBuilder builder = new StreamsBuilder();
        final Serde<ValidateData> validateDataSerdes = Serdes.serdeFrom(new ValidateDataSerializer(), new ValidateDataDeserializer());

        KStream<String, ValidateData> streamToES = builder.stream(kafkaConfiguration.getRetryTopic(), Consumed.with(Serdes.String(), validateDataSerdes));
        ESBuffer esBuffer = new ESBuffer(esClient, esBufferConfiguration, esConfiguration);

        RetryToElasticsearchProcessor retryToElasticsearchProcessor = new RetryToElasticsearchProcessor(esBuffer, esErrorRetryWriter);
        streamToES.process(() -> retryToElasticsearchProcessor);

        retryStream = new KafkaStreams(builder.build(), KafkaUtils.createKStreamProperties(INPUT_PROCESS_RETRY, kafkaConfiguration.getBootstrapServers()));
        Runtime.getRuntime().addShutdownHook(new Thread(retryStream::close));
    }

    public void enable() {
        log.info("Enabling retry importer");
        retryStream.start();
    }

    public void disable() {
        log.info("Disabling retry importer");
        retryStream.close();
    }
}