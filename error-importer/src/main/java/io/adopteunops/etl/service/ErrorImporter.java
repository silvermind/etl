package io.adopteunops.etl.service;

import io.adopteunops.etl.admin.KafkaAdminService;
import io.adopteunops.etl.config.KafkaConfiguration;
import io.adopteunops.etl.domain.ErrorData;
import io.adopteunops.etl.serdes.GenericDeserializer;
import io.adopteunops.etl.serdes.GenericSerializer;
import io.adopteunops.etl.utils.KafkaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = false)
@Slf4j
public class ErrorImporter {


    private static final String INPUT_PROCESS_ERROR = "es-error";
    private final KafkaStreams errorStream;

    public ErrorImporter(ErrorToElasticsearchProcessor elasticsearchProcessor, KafkaConfiguration kafkaConfiguration, KafkaAdminService kafkaAdminService) {
        kafkaAdminService.buildTopic(kafkaConfiguration.getErrorTopic());

        StreamsBuilder builder = new StreamsBuilder();
        final Serde<ErrorData> errorDataSerde = Serdes.serdeFrom(new GenericSerializer<>(), new GenericDeserializer<>(ErrorData.class));

        KStream<String, ErrorData> streamToES = builder.stream(kafkaConfiguration.getErrorTopic(), Consumed.with(Serdes.String(), errorDataSerde));

        streamToES.process(() -> elasticsearchProcessor);

        errorStream = new KafkaStreams(builder.build(), KafkaUtils.createKStreamProperties(INPUT_PROCESS_ERROR, kafkaConfiguration.getBootstrapServers()));
        Runtime.getRuntime().addShutdownHook(new Thread(errorStream::close));
    }

    public void enable() {
        log.info("Enabling error importer");
        errorStream.start();
    }

    public void disable() {
        log.info("Disabling error importer");
        errorStream.close();
    }
}
