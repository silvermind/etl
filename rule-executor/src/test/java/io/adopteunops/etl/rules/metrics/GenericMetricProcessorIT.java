package io.adopteunops.etl.rules.metrics;

import com.fasterxml.jackson.databind.JsonNode;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.kafka.KafkaUnit;
import io.adopteunops.etl.kafka.KafkaUnitRule;
import io.adopteunops.etl.rules.UtilsValidator;
import io.adopteunops.etl.rules.metrics.domain.Keys;
import io.adopteunops.etl.rules.metrics.domain.MetricResult;
import io.adopteunops.etl.rules.metrics.udaf.AggregateFunction;
import io.adopteunops.etl.serdes.ValidateDataSerializer;
import io.adopteunops.etl.utils.JSONUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Windowed;
import org.junit.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericMetricProcessorIT {
    @ClassRule
    public static KafkaUnitRule kafkaUnitRule = new KafkaUnitRule();

    private Properties properties = new Properties();

    private KafkaStreams streams;

    @Before
    public void beforeEachTest() {
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUnitRule.getKafkaUnit().getKafkaConnect());
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1);
        properties.put(StreamsConfig.POLL_MS_CONFIG, 10);
        properties.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, MessageTimestampExtractor.class);

    }

    @After
    public void closeStream() {
        if (streams != null) {
            streams.close();
        }
    }

    @Test
    public void shouldComputeMin() {

        List<ValidateData> input = Arrays.asList(
                toValidateData("{\"project\":\"myproject\",\"type\":\"something\",\"duration\": 1}"),
                toValidateData("{\"project\":\"myproject\",\"type\":\"somethingelse\",\"duration\": 10}")
        );
        String destTopic = "min-dest";
        GenericMetricProcessor minDuration = new GenericMetricProcessor("min", "dsl", "min-src") {
            @Override
            public void routeResult(KStream<Keys, MetricResult> result) {
                toKafkaTopic(result, destTopic);
            }

            @Override
            protected AggregateFunction aggInitializer() {
                return aggFunction("min");
            }

            @Override
            protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {
                return aggregateTumblingWindow(kGroupedStream, 1, TimeUnit.SECONDS);
            }

            @Override
            protected Double mapValues(JsonNode value) {
                return value.path("duration").asDouble();
            }
        };
        List<KafkaUnit.Message<Keys, MetricResult>> resultInDestTopic = executeMetricStream(input, minDuration, destTopic);
        assertThat(resultInDestTopic).hasSize(1);
        assertThat(resultInDestTopic.get(0).getKey().getRuleName()).isEqualTo("min");
        assertThat(resultInDestTopic.get(0).getKey().getRuleDSL()).isEqualTo("dsl");
        assertThat(resultInDestTopic.get(0).getKey().getProject()).isEqualTo("myproject");
        assertThat(resultInDestTopic.get(0).getValue().getResult()).isEqualTo(1);
    }

    @Test
    public void shouldComputeMax() {

        List<ValidateData> input = Arrays.asList(
                toValidateData("{\"project\":\"myproject\",\"type\":\"something\",\"duration\": 1}"),
                toValidateData("{\"project\":\"myproject\",\"type\":\"somethingelse\",\"duration\": 10}")
        );
        String destTopic = "max-dest";
        GenericMetricProcessor minDuration = new GenericMetricProcessor("max", "dsl", "max-src") {

            @Override
            public void routeResult(KStream<Keys, MetricResult> result) {
                toKafkaTopic(result, destTopic);
            }

            @Override
            protected AggregateFunction aggInitializer() {
                return aggFunction("max");
            }

            @Override
            protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {
                return aggregateTumblingWindow(kGroupedStream, 1, TimeUnit.SECONDS);
            }

            @Override
            protected Double mapValues(JsonNode value) {
                return value.path("duration").asDouble();
            }
        };
        List<KafkaUnit.Message<Keys, MetricResult>> resultInDestTopic = executeMetricStream(input, minDuration, destTopic);
        assertThat(resultInDestTopic).hasSize(1);
        KafkaUnit.Message<Keys, MetricResult> result1 = resultInDestTopic.get(0);
        assertThat(result1.getKey().getRuleName()).isEqualTo("max");
        assertThat(result1.getKey().getRuleDSL()).isEqualTo("dsl");
        assertThat(result1.getKey().getProject()).isEqualTo("myproject");
        assertThat(result1.getValue().getResult()).isEqualTo(10);
    }

    @Test
    public void shouldComputeAvg() {

        List<ValidateData> input = Arrays.asList(
                toValidateData("{\"project\":\"myproject\",\"type\":\"something\",\"duration\": 1}"),
                toValidateData("{\"project\":\"myproject\",\"type\":\"somethingelse\",\"duration\": 9}")
        );


        String destTopic = "avg-dest";
        GenericMetricProcessor minDuration = new GenericMetricProcessor("avg", "dsl", "avg-src") {

            @Override
            public void routeResult(KStream<Keys, MetricResult> result) {
                toKafkaTopic(result, destTopic);
            }

            @Override
            protected AggregateFunction aggInitializer() {
                return aggFunction("avg");
            }

            @Override
            protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {
                return aggregateTumblingWindow(kGroupedStream, 1, TimeUnit.SECONDS);
            }

            @Override
            protected Double mapValues(JsonNode value) {
                return value.path("duration").asDouble();
            }
        };
        List<KafkaUnit.Message<Keys, MetricResult>> resultInDestTopic = executeMetricStream(input, minDuration, destTopic);
        assertThat(resultInDestTopic).hasSize(1);
        KafkaUnit.Message<Keys, MetricResult> result1 = resultInDestTopic.get(0);
        assertThat(result1.getKey().getRuleName()).isEqualTo("avg");
        assertThat(result1.getKey().getRuleDSL()).isEqualTo("dsl");
        assertThat(result1.getKey().getProject()).isEqualTo("myproject");
        assertThat(result1.getValue().getResult()).isEqualTo(5);
    }

    @Test
    public void shouldComputeSum() {

        List<ValidateData> input = Arrays.asList(
                toValidateData("{\"project\":\"myproject\",\"type\":\"something\",\"duration\": 1}"),
                toValidateData("{\"project\":\"myproject\",\"type\":\"somethingelse\",\"duration\": 9}")
        );


        String destTopic = "sum-dest";
        GenericMetricProcessor minDuration = new GenericMetricProcessor("sum", "dsl", "sum-src") {

            @Override
            public void routeResult(KStream<Keys, MetricResult> result) {
                toKafkaTopic(result, destTopic);
            }

            @Override
            protected AggregateFunction aggInitializer() {
                return aggFunction("sum");
            }

            @Override
            protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {
                return aggregateTumblingWindow(kGroupedStream, 1, TimeUnit.SECONDS);
            }

            @Override
            protected Double mapValues(JsonNode value) {
                return value.path("duration").asDouble();
            }
        };
        List<KafkaUnit.Message<Keys, MetricResult>> resultInDestTopic = executeMetricStream(input, minDuration, destTopic);
        assertThat(resultInDestTopic).hasSize(1);
        KafkaUnit.Message<Keys, MetricResult> result1 = resultInDestTopic.get(0);
        assertThat(result1.getKey().getRuleName()).isEqualTo("sum");
        assertThat(result1.getKey().getRuleDSL()).isEqualTo("dsl");
        assertThat(result1.getKey().getProject()).isEqualTo("myproject");
        assertThat(result1.getValue().getResult()).isEqualTo(10);
    }

    @Test
    public void shouldComputeSumGroupByType() {

        List<ValidateData> input = Arrays.asList(
                toValidateData("{\"project\":\"myproject\",\"type\":\"something\",\"duration\": 1}"),
                toValidateData("{\"project\":\"myproject\",\"type\":\"somethingelse\",\"duration\": 9}")
        );


        String destTopic = "sum-groupby-dest";
        GenericMetricProcessor minDuration = new GenericMetricProcessor("sum-groupby", "dsl", "sum-groupby-src") {

            @Override
            public void routeResult(KStream<Keys, MetricResult> result) {
                toKafkaTopic(result, destTopic);
            }

            @Override
            protected AggregateFunction aggInitializer() {
                return aggFunction("sum");
            }

            @Override
            protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {
                return aggregateTumblingWindow(kGroupedStream, 1, TimeUnit.SECONDS);
            }

            @Override
            protected Double mapValues(JsonNode value) {
                return value.path("duration").asDouble();
            }

            @Override
            protected Keys selectKey(String key, JsonNode value) {
                Keys keys = super.selectKey(key, value);
                keys.addKey("type", value.get("type").asText());
                return keys;
            }
        };
        List<KafkaUnit.Message<Keys, MetricResult>> resultInDestTopic = executeMetricStream(input, minDuration, destTopic);
        assertThat(resultInDestTopic).hasSize(2);
        KafkaUnit.Message<Keys, MetricResult> result1 = resultInDestTopic.get(0);
        assertThat(result1.getKey().getRuleName()).isEqualTo("sum-groupby");
        assertThat(result1.getKey().getRuleDSL()).isEqualTo("dsl");
        assertThat(result1.getKey().getProject()).isEqualTo("myproject");
        assertThat(result1.getKey().getKeys().get("type")).isEqualTo("something");
        assertThat(result1.getValue().getResult()).isEqualTo(1);
        //assertThat(resultInDestTopic.get("type:something")).isEqualTo(1d);
        assertThat(resultInDestTopic).hasSize(2);
        KafkaUnit.Message<Keys, MetricResult> result2 = resultInDestTopic.get(1);
        assertThat(result2.getKey().getRuleName()).isEqualTo("sum-groupby");
        assertThat(result2.getKey().getRuleDSL()).isEqualTo("dsl");
        assertThat(result2.getKey().getProject()).isEqualTo("myproject");
        assertThat(result2.getKey().getKeys().get("type")).isEqualTo("somethingelse");
        assertThat(result2.getValue().getResult()).isEqualTo(9);
    }

    @Test
    public void shouldComputeSumWithFilter() {

        List<ValidateData> input = Arrays.asList(
                toValidateData("{\"project\":\"myproject\",\"type\":\"something\",\"duration\": 42}"),
                toValidateData("{\"project\":\"myproject\",\"type\":\"somethingelse\",\"duration\": 1}"),
                toValidateData("{\"project\":\"myproject\",\"type\":\"somethingelse\",\"duration\": 9}")
        );


        String destTopic = "sum-withfilter-dest";
        GenericMetricProcessor minDuration = new GenericMetricProcessor("sum-withfilter", "dsl", "sum-withfilter-src") {
            @Override
            public void routeResult(KStream<Keys, MetricResult> result) {
                toKafkaTopic(result, destTopic);
            }

            @Override
            protected AggregateFunction aggInitializer() {
                return aggFunction("sum");
            }

            @Override
            protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {
                return aggregateTumblingWindow(kGroupedStream, 1, TimeUnit.SECONDS);
            }

            @Override
            protected Double mapValues(JsonNode value) {
                return value.path("duration").asDouble();
            }

            @Override
            protected boolean filter(String key, JsonNode value) {
                return evaluate("CONTAINS", UtilsValidator.get(value, "type"), "else");
            }
        };
        List<KafkaUnit.Message<Keys, MetricResult>> resultInDestTopic = executeMetricStream(input, minDuration, destTopic);
        assertThat(resultInDestTopic).hasSize(1);
        KafkaUnit.Message<Keys, MetricResult> result1 = resultInDestTopic.get(0);
        assertThat(result1.getKey().getRuleName()).isEqualTo("sum-withfilter");
        assertThat(result1.getKey().getRuleDSL()).isEqualTo("dsl");
        assertThat(result1.getKey().getProject()).isEqualTo("myproject");
        assertThat(result1.getValue().getResult()).isEqualTo(10);
    }

    @Test
    @Ignore("slow")
    public void shouldComputeMinWithTumblingWindow() {

        List<ValidateData> inputStartTime = Arrays.asList(
                toValidateData("{\"project\":\"myproject\",\"type\":\"something\",\"duration\": 2}"),
                toValidateData("{\"project\":\"myproject\",\"type\":\"somethingelse\",\"duration\": 10}")
        );
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        List<ValidateData> inputsDelayed = Arrays.asList(
                toValidateData("{\"project\":\"myproject\",\"type\":\"something\",\"duration\": 1}"),
                toValidateData("{\"project\":\"myproject\",\"type\":\"somethingelse\",\"duration\": 8}")
        );
        List<ValidateData> input = new ArrayList<>();
        input.addAll(inputStartTime);
        input.addAll(inputsDelayed);

        String destTopic = "min-dest";
        GenericMetricProcessor minDuration = new GenericMetricProcessor("min", "dsl", "min-src") {
            @Override
            public void routeResult(KStream<Keys, MetricResult> result) {
                toKafkaTopic(result, destTopic);
            }

            @Override
            protected AggregateFunction aggInitializer() {
                return aggFunction("min");
            }

            @Override
            protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {
                return aggregateTumblingWindow(kGroupedStream, 1, TimeUnit.SECONDS);
            }

            @Override
            protected Double mapValues(JsonNode value) {
                return value.path("duration").asDouble();
            }
        };
        List<KafkaUnit.Message<Keys, MetricResult>> resultInDestTopic = executeMetricStream(input, minDuration, destTopic);
        assertThat(resultInDestTopic).hasSize(2);
        assertThat(resultInDestTopic.get(0).getKey().getRuleName()).isEqualTo("min");
        assertThat(resultInDestTopic.get(0).getKey().getRuleDSL()).isEqualTo("dsl");
        assertThat(resultInDestTopic.get(0).getKey().getProject()).isEqualTo("myproject");
        assertThat(resultInDestTopic.get(0).getValue().getResult()).isEqualTo(2);
        assertThat(resultInDestTopic.get(1).getKey().getRuleName()).isEqualTo("min");
        assertThat(resultInDestTopic.get(1).getKey().getRuleDSL()).isEqualTo("dsl");
        assertThat(resultInDestTopic.get(1).getKey().getProject()).isEqualTo("myproject");
        assertThat(resultInDestTopic.get(1).getValue().getResult()).isEqualTo(1);

    }


    private List<KafkaUnit.Message<Keys, MetricResult>> executeMetricStream(List<ValidateData> input, GenericMetricProcessor metricProcessor, String destTopic) {
        kafkaUnitRule.getKafkaUnit().createTopic(metricProcessor.getSrcTopic());
        kafkaUnitRule.getKafkaUnit().createTopic(destTopic);

        kafkaUnitRule.getKafkaUnit().sendMessages(ValidateDataSerializer.class.getName(), ValidateDataSerializer.class.getName(),
                toProducerRecords(metricProcessor.getSrcTopic(), input));


        executeStream(metricProcessor.buildStream(properties));

        MetricResultMessageExtractor messageExtractor = new MetricResultMessageExtractor();
        return kafkaUnitRule.getKafkaUnit().readMessages(destTopic, 10, messageExtractor);
    }

    private List<ProducerRecord<ValidateData, ValidateData>> toProducerRecords(String topic, List<ValidateData> validateData) {
        return validateData.stream().map(e -> new ProducerRecord<>(topic, e, e)).collect(Collectors.toList());
    }

    private ValidateData toValidateData(String rawJson) {
        return ValidateData.builder().timestamp(new Date()).jsonValue(JSONUtils.getInstance().parse(rawJson)).build();
    }

    private void executeStream(KafkaStreams streams) {
        this.streams = streams;
        streams.cleanUp();
        streams.start();
    }


}
