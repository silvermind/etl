package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.ProcessConsumer;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.rules.filters.GenericFilter;
import io.adopteunops.etl.serdes.ValidateDataDeserializer;
import io.adopteunops.etl.serdes.ValidateDataSerializer;
import io.adopteunops.etl.service.processor.LoggingProcessor;
import io.adopteunops.etl.service.processor.ValidateDataToElasticSearchProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.List;

import static io.adopteunops.etl.utils.KafkaUtils.createKStreamProperties;

@Slf4j
public class ProcessStreamService extends AbstractStreamProcess {
    private final ESErrorRetryWriter esErrorRetryWriter;
    private final ValidateDataToElasticSearchProcessor elasticSearchProcessor;
    private final List<GenericFilter> genericFilters;

    public ProcessStreamService(GenericValidator genericValidator, GenericTransformator transformValidator, GenericParser genericParser, ProcessConsumer processConsumer, List<GenericFilter> genericFilters, ESErrorRetryWriter esErrorRetryWriter, ValidateDataToElasticSearchProcessor elasticSearchProcessor) {
        super(genericValidator, transformValidator, genericParser, processConsumer);
        this.esErrorRetryWriter = esErrorRetryWriter;
        this.elasticSearchProcessor = elasticSearchProcessor;
        this.genericFilters = genericFilters;
    }

    public void createStreamProcess() {
        log.info("create Stream Process for treat INPUT");
        createStreamInput(getProcessConsumer().getProcessInput().getTopicInput(), getProcessConsumer().getIdProcess() + TOPIC_PARSED_PROCESS);
        log.info("create Stream Process for valid transform and filters");
        createStreamValidAndTransformAndFilter(getProcessConsumer().getIdProcess() + TOPIC_PARSED_PROCESS, getProcessConsumer().getIdProcess() + TOPIC_TREAT_PROCESS);
        switch (getProcessConsumer().getProcessOutput().getTypeOutput()) {
            case ELASTICSEARCH:
                log.info("create Stream Process for treat ES");
                createStreamEs(getProcessConsumer().getIdProcess() + TOPIC_TREAT_PROCESS);
                break;
            case KAFKA:
                createStreamKafka(getProcessConsumer().getIdProcess() + TOPIC_TREAT_PROCESS);
                break;
            case SYSTEM_OUT:
                createStreamSystemOut(getProcessConsumer().getIdProcess() + TOPIC_TREAT_PROCESS);
                break;
            default:
                log.error("TypeOut not managed {}", getProcessConsumer().getProcessOutput());
                break;
        }
    }

    private void createStreamInput(String inputTopic, String outputTopic) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> streamInput = builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));

        KStream<String, String> streamParsed = streamInput.map((key, value) -> {
            readKafkaCount.labels(getProcessConsumer().getName()).inc();
            String resultParsing = getGenericParser().apply(value, getProcessConsumer());
            return new KeyValue<>("input", resultParsing);
        }).filter((key, value) -> value != null);

        final Serde<String> stringSerdes = Serdes.String();

        streamParsed.to(outputTopic, Produced.with(stringSerdes, stringSerdes));

        KafkaStreams streams = new KafkaStreams(builder.build(), createKStreamProperties(getProcessConsumer().getIdProcess() + INPUT_PROCESS, getBootstrapServer()));
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
        addStreams(getProcessConsumer().getIdProcess() + INPUT_PROCESS, streams);
    }

    private void createStreamValidAndTransformAndFilter(String inputTopic, String outputTopic) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> streamInput = builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));
        String applicationId = getProcessConsumer().getIdProcess() + VALIDATE_PROCESS;
        KStream<String, ValidateData> streamValidation = streamInput.map((key, value) -> {
            String resultTransformer = getGenericTransformator().apply(value, getProcessConsumer());
            ValidateData item = getGenericValidator().process(resultTransformer, getProcessConsumer());
            transformationAndValidationCount.labels(getProcessConsumer().getName()).inc();
            return new KeyValue<>(item.type, item);
        }).filter((key, value) -> {
            //Validation
            if (!value.success) {
                //produce to errorTopic
                esErrorRetryWriter.sendToErrorTopic(applicationId, value.toErrorData());
                return false;
            }
            //FILTER
            return processFilter(value);
        });

        final Serde<String> stringSerdes = Serdes.String();
        final Serde<ValidateData> validateDataSerdes = Serdes.serdeFrom(new ValidateDataSerializer(), new ValidateDataDeserializer());

        streamValidation.to(outputTopic, Produced.with(stringSerdes, validateDataSerdes));


        KafkaStreams streams = new KafkaStreams(builder.build(), createKStreamProperties(applicationId, getBootstrapServer()));
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
        addStreams(applicationId, streams);
    }

    private Boolean processFilter(ValidateData item) {
        for (GenericFilter genericFilter : genericFilters) {
            if (!genericFilter.filter(item.jsonValue)) {
                return false;
            }
        }
        return true;

    }

    public void createStreamEs(String inputTopic) {

        StreamsBuilder builder = new StreamsBuilder();
        final Serde<ValidateData> validateDataSerdes = Serdes.serdeFrom(new ValidateDataSerializer(), new ValidateDataDeserializer());

        KStream<String, ValidateData> streamToES = builder.stream(inputTopic, Consumed.with(Serdes.String(), validateDataSerdes));
        streamToES.process(() -> elasticSearchProcessor);

        KafkaStreams streams = new KafkaStreams(builder.build(), createKStreamProperties(getProcessConsumer().getIdProcess() + ES_PROCESS, getBootstrapServer()));
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
        addStreams(getProcessConsumer().getIdProcess() + ES_PROCESS, streams);
    }

    public void createStreamSystemOut(String inputTopic) {

        StreamsBuilder builder = new StreamsBuilder();
        final Serde<ValidateData> validateDataSerdes = Serdes.serdeFrom(new ValidateDataSerializer(), new ValidateDataDeserializer());

        builder.stream(inputTopic, Consumed.with(Serdes.String(), validateDataSerdes)).process(() -> new LoggingProcessor<>());

        KafkaStreams streams = new KafkaStreams(builder.build(), createKStreamProperties(getProcessConsumer().getIdProcess() + SYSOUT_PROCESS, getBootstrapServer()));
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
        addStreams(getProcessConsumer().getIdProcess() + SYSOUT_PROCESS, streams);
    }


    public void createStreamKafka(String inputTopic) {

        StreamsBuilder builder = new StreamsBuilder();
        final Serde<ValidateData> validateDataSerdes = Serdes.serdeFrom(new ValidateDataSerializer(), new ValidateDataDeserializer());
        final Serde<String> stringSerdes = Serdes.String();

        builder.stream(inputTopic, Consumed.with(Serdes.String(), validateDataSerdes))
                .map((key, value) -> {
                    return new KeyValue<>(key, value.value);
                })
                .to(getProcessConsumer().getProcessOutput().getParameterOutput().getTopicOut(), Produced.with(stringSerdes, stringSerdes));

        KafkaStreams streams = new KafkaStreams(builder.build(), createKStreamProperties(getProcessConsumer().getIdProcess() + KAFKA_PROCESS, getBootstrapServer()));
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
        addStreams(getProcessConsumer().getIdProcess() + KAFKA_PROCESS, streams);
    }

}
