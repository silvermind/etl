package io.adopteunops.etl.rules.metrics;

import com.fasterxml.jackson.databind.JsonNode;
import io.adopteunops.etl.domain.RetentionLevel;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.rules.functions.FunctionRegistry;
import io.adopteunops.etl.rules.metrics.domain.Keys;
import io.adopteunops.etl.rules.metrics.domain.MetricResult;
import io.adopteunops.etl.rules.metrics.processor.MetricsElasticsearchProcessor;
import io.adopteunops.etl.rules.metrics.processor.MetricsEmailProcessor;
import io.adopteunops.etl.rules.metrics.serdes.MetricsSerdes;
import io.adopteunops.etl.rules.metrics.udaf.AggregateFunction;
import io.adopteunops.etl.serdes.ValidateDataDeserializer;
import io.adopteunops.etl.serdes.ValidateDataSerializer;
import io.adopteunops.etl.service.processor.LoggingProcessor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.state.SessionStore;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.ApplicationContext;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public abstract class GenericMetricProcessor {

    private final String name;
    private final String dsl;
    private final String srcTopic;
    @Setter
    private ApplicationContext applicationContext;


    protected GenericMetricProcessor(String name, String dsl, String srcTopic) {
        this.name = name;
        this.dsl = dsl;
        this.srcTopic = srcTopic;
    }

    public KafkaStreams buildStream(Properties props) {
        StreamsBuilder builder = new StreamsBuilder();

        final Serde<ValidateData> validateDataSerdes = Serdes.serdeFrom(new ValidateDataSerializer(), new ValidateDataDeserializer());


        KStream<String, ValidateData> stream = builder.stream(srcTopic, Consumed.with(Serdes.String(), validateDataSerdes));


        KGroupedStream<Keys, Double> filteredElementsGroupByKeys = stream
                .map(this::convertToJsonNode)
                .filter(this::filter)
                .selectKey(this::selectKey)
                .mapValues(this::mapValues)
                .groupByKey(Serialized.with(MetricsSerdes.keysSerde(), Serdes.Double()));

        KTable<Windowed<Keys>, Double> aggregateResults = aggregate(filteredElementsGroupByKeys);

        KStream<Keys, MetricResult> result = aggregateResults
                .toStream()
                .map((key, value) -> new KeyValue<>(key.key(), new MetricResult(key, value)));

        routeResult(result);

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, name + "-stream");

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        return streams;
    }

    public abstract void routeResult(KStream<Keys, MetricResult> result);

    protected void toKafkaTopic(KStream<Keys, MetricResult> result, String destTopic) {
        result.to(destTopic, Produced.with(MetricsSerdes.keysSerde(), MetricsSerdes.metricResultSerdes()));
    }

    protected void toElasticsearch(KStream<Keys, MetricResult> result, RetentionLevel retentionLevel) {
        MetricsElasticsearchProcessor metricsElasticsearchProcessor = applicationContext.getBean(MetricsElasticsearchProcessor.class, retentionLevel);
        result.process(() -> metricsElasticsearchProcessor);
    }

    protected void toSystemOut(KStream<Keys, MetricResult> result) {
        AbstractProcessor abstractProcessor = applicationContext.getBean(LoggingProcessor.class);
        result.process(() -> abstractProcessor);
    }

    protected void toEmail(KStream<Keys, MetricResult> result, String destinationEmail) {
        AbstractProcessor abstractProcessor = applicationContext.getBean(MetricsEmailProcessor.class, destinationEmail);
        result.process(() -> abstractProcessor);
    }

    protected AggregateFunction aggFunction(String aggFunctionName) {
        return UDAFRegistry.getInstance().get(aggFunctionName);
    }

    // MANDATORY METHODS
    protected abstract AggregateFunction aggInitializer();

    protected abstract Double mapValues(JsonNode value);

    protected abstract KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream);


    // OPTIONAL METHODS
    protected Double operation(AggregateFunction myOperation) {
        return (Double) myOperation.compute();
    }

    protected Keys selectKey(String key, JsonNode value) {
        return new Keys(name, dsl, value.path("project").asText());
    }

    protected boolean filter(String key, JsonNode value) {
        return true;
    }

    // INTERNALS
    private Materialized<Keys, AggregateFunction, WindowStore<Bytes, byte[]>> materializedMathOperationTimeWindow() {
        return Materialized.<Keys, AggregateFunction, WindowStore<Bytes, byte[]>>as("aggregated-stream-store")
                .withKeySerde(MetricsSerdes.keysSerde())
                .withValueSerde(MetricsSerdes.aggFunctionSerdes());
    }

    private Materialized<Keys, AggregateFunction, SessionStore<Bytes, byte[]>> materializedMathOperationSessionWindow() {
        return Materialized.<Keys, AggregateFunction, SessionStore<Bytes, byte[]>>as("aggregated-stream-store")
                .withKeySerde(MetricsSerdes.keysSerde())
                .withValueSerde(MetricsSerdes.aggFunctionSerdes());
    }

    private KeyValue<String, JsonNode> convertToJsonNode(String key, ValidateData value) {
        return new KeyValue<>(null, value.jsonValue);
    }

    protected boolean evaluate(String functionName, Object... args) {
        return FunctionRegistry.getInstance().evaluate(functionName, args);
    }


    protected KTable<Windowed<Keys>, Double> aggregateHoppingWindow(KGroupedStream<Keys, Double> kGroupedStream,
                                                                    long size,
                                                                    TimeUnit sizeUnit,
                                                                    long advanceBy,
                                                                    TimeUnit advanceByUnit) {
        TimeWindowedKStream<Keys, Double> windowedKStream = kGroupedStream
                .windowedBy(TimeWindows.of(sizeUnit.toMillis(size)).advanceBy(advanceByUnit.toMillis(advanceBy)));
        KTable<Windowed<Keys>, AggregateFunction> aggregate = windowedKStream.aggregate(
                this::aggInitializer,
                (k, v, agg) -> agg.addValue(v),
                materializedMathOperationTimeWindow());


        return aggregate.mapValues(this::operation);
    }

    protected KTable<Windowed<Keys>, Double> aggregateTumblingWindow(KGroupedStream<Keys, Double> kGroupedStream,
                                                                     long size,
                                                                     TimeUnit sizeUnit) {
        TimeWindowedKStream<Keys, Double> windowedKStream = kGroupedStream
                .windowedBy(TimeWindows.of(sizeUnit.toMillis(size)));
        KTable<Windowed<Keys>, AggregateFunction> aggregate = windowedKStream.aggregate(
                this::aggInitializer,
                (k, v, agg) -> agg.addValue(v),
                materializedMathOperationTimeWindow());

        return aggregate.mapValues(this::operation);
    }

    protected KTable<Windowed<Keys>, Double> aggregateSessionWindow(KGroupedStream<Keys, Double> kGroupedStream,
                                                                    long gap,
                                                                    TimeUnit sizeUnit) {
        SessionWindowedKStream<Keys, Double> windowedKStream = kGroupedStream
                .windowedBy(SessionWindows.with(sizeUnit.toMillis(gap)));
        KTable<Windowed<Keys>, AggregateFunction> aggregate = windowedKStream.aggregate(
                this::aggInitializer,
                (k, v, agg) -> agg.addValue(v),
                (key, aggOne, aggTwo) -> aggOne.merge(aggTwo),
                materializedMathOperationSessionWindow());

        return aggregate.mapValues(this::operation);
    }

}
