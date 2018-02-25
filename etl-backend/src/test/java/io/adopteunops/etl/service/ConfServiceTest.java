package io.adopteunops.etl.service;

import io.adopteunops.etl.config.ESConfiguration;
import io.adopteunops.etl.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Slf4j
public class ConfServiceTest {

    private final ConfService confService;

    public ConfServiceTest() {
        ESConfiguration esConfiguration = new ESConfiguration();
        RestHighLevelClient restHighLevelClient = mock(RestHighLevelClient.class);
        confService = new ConfService(restHighLevelClient, esConfiguration);
    }

    private Translate generateFilterActionTranslate(TypeFilter type) {
        return Translate.builder()
                .key("keyTranslate")
                .value("valueTranslate")
                .action(type)
                .keyPattern("MYPATTERN2")
                .build();
    }

    @Test
    public void filterAction_Date() {
        String result = confService.filterAction(generateFilterActionTranslate(TypeFilter.DATE));
        assertThat(result).isEqualTo(" date { match => [\"keyTranslate\", valueTranslate] }\n");
    }

    @Test
    public void filterAction_Add() {
        String result = confService.filterAction(generateFilterActionTranslate(TypeFilter.ADD));
        assertThat(result).isEqualTo(" mutate { add_field => {\"keyTranslate\", \"valueTranslate\"} }\n");
    }

    @Test
    public void filterAction_Grok() {
        String result = confService.filterAction(generateFilterActionTranslate(TypeFilter.GROK));
        assertThat(result).isEqualTo(" grok { match => {\"keyTranslate\" => \"MYPATTERN2\"} }\n");
    }

    private Translate generateFilterTranslate(TypeFilter type, TypeCondition tc) {
        return Translate.builder()
                .key("keyTranslate")
                .value("valueTranslate")
                .action(type)
                .keyPattern("MYPATTERN2")
                .typeCondition(tc)
                .build();
    }

    @Test
    public void filter_CondNull() {
        String result = confService.generateFilter(generateFilterTranslate(TypeFilter.GROK, null));
        assertThat(result).isEqualTo(" grok { match => {\"keyTranslate\" => \"MYPATTERN2\"} }\n");
    }

    @Test
    public void filter_CondNotNullButCheckTrue() {
        String result = confService.generateFilter(generateFilterTranslate(TypeFilter.GROK, TypeCondition.builder().condition("condition").checkPresent(true).isPresence(true).build()));
        assertThat(result).isEqualTo(" if [condition] { \n grok { match => {\"keyTranslate\" => \"MYPATTERN2\"} }\n }\n");
    }

    @Test
    public void filter_CondNotNullButCheckFalse() {
        String result = confService.generateFilter(generateFilterTranslate(TypeFilter.GROK, TypeCondition.builder().condition("condition").checkPresent(true).isPresence(false).build()));
        assertThat(result).isEqualTo(" if ![condition] { \n grok { match => {\"keyTranslate\" => \"MYPATTERN2\"} }\n }\n");
    }

    @Test
    public void input_beatTypeNull() {
        String result = confService.generateInput(ConfigurationHost.builder().typeInput(TypeInput.BEATS).port("8080").build());
        assertThat(result).isEqualTo(" beats { port => 8080 \n }\n");
    }

    @Test
    public void input_beatTypeNotNull() {
        String result = confService.generateInput(ConfigurationHost.builder().typeInput(TypeInput.BEATS).port("8080").typeForced("toto").build());
        assertThat(result).isEqualTo(" beats { port => 8080 \n type => \"toto\" }\n");
    }

    @Test
    public void input_tcpTypeNull() {
        String result = confService.generateInput(ConfigurationHost.builder().typeInput(TypeInput.TCP).port("8080").build());
        assertThat(result).isEqualTo(" tcp { port => 8080 \n }\n");
    }

    @Test
    public void input_tcpTypeNotNull() {
        String result = confService.generateInput(ConfigurationHost.builder().typeInput(TypeInput.TCP).port("8080").typeForced("toto").build());
        assertThat(result).isEqualTo(" tcp { port => 8080 \n type => \"toto\" }\n");
    }

    @Test
    public void input_tcpTypeNotNullCodecNotNull() {
        String result = confService.generateInput(ConfigurationHost.builder().typeInput(TypeInput.TCP).port("8080").typeForced("toto").codec("json").build());
        assertThat(result).isEqualTo(" tcp { port => 8080 \n codec => \"json\" \n type => \"toto\" }\n");
    }

    @Test
    public void input_fileTypeNull() {
        String result = confService.generateInput(ConfigurationHost.builder().typeInput(TypeInput.FILE).path("myubberpath/toto/titi/tata").build());
        assertThat(result).isEqualTo(" file { path => \"myubberpath/toto/titi/tata\" \n }\n");
    }

    @Test
    public void input_fileTypeNotNull() {
        String result = confService.generateInput(ConfigurationHost.builder().typeInput(TypeInput.FILE).path("myubberpath/toto/titi/tata").typeForced("toto").build());
        assertThat(result).isEqualTo(" file { path => \"myubberpath/toto/titi/tata\" \n type => \"toto\" }\n");
    }

    @Test
    public void output_Kafka() {
        String result = confService.generateOutput(ConfigurationOutput.builder().host("kafka:9092").topic("mytopic").codec("json").build());
        assertThat(result).isEqualTo(" kafka { \n bootstrap_servers => \"kafka:9092\" \n topic_id => \"mytopic\" \n codec => \"json\" \n }\n");
    }

    @Test
    public void generateSimpleFilterWithoutCondition() {
        ConfigurationHost in = ConfigurationHost.builder().typeInput(TypeInput.TCP).port("8080").typeForced("toto").codec("json").build();
        List<ConfigurationHost> inputList = new ArrayList<>();
        inputList.add(in);
        String result = confService.generateConfig(ConfigurationLogstash.builder()
                .input(inputList)
                .output(ConfigurationOutput.builder().host("kafka").port("9092").topic("mytopic").codec("json").build())
                .build());
        assertThat(result).isEqualTo(" input { \n tcp { port => 8080 \n codec => \"json\" \n type => \"toto\" }\n} \n filter {} \n output { \n kafka { \n bootstrap_servers => \"kafka:9092\" \n topic_id => \"mytopic\" \n codec => \"json\" \n }\n} \n");
    }

    @Test
    public void generateSimpleFilterWithCondition() {
        ConfigurationHost in = ConfigurationHost.builder().typeInput(TypeInput.TCP).port("8080").typeForced("toto").codec("json").build();
        List<ConfigurationHost> inputList = new ArrayList<>();
        inputList.add(in);
        String result = confService.generateConfig(ConfigurationLogstash.builder()
                .input(inputList)
                .output(ConfigurationOutput.builder().host("kafka").port("9092").topic("mytopic").codec("json").build())
                .build());
        assertThat(result).isEqualTo(" input { \n tcp { port => 8080 \n codec => \"json\" \n type => \"toto\" }\n} \n filter {} \n output { \n kafka { \n bootstrap_servers => \"kafka:9092\" \n topic_id => \"mytopic\" \n codec => \"json\" \n }\n} \n");

    }

}
