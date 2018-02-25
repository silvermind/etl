package io.adopteunops.etl.service;


import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import io.adopteunops.etl.config.ESConfiguration;
import io.adopteunops.etl.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class ConfService {

    public HashMap<String, ConfigurationLogstash> map = new HashMap<>();

    private final String RETURN_BRACE = " }\n";
    private final String RETURN = " \n";
    private final RestHighLevelClient restHighLevelClient;
    private final ESConfiguration esConfiguration;

    public ConfService(RestHighLevelClient restHighLevelClient, ESConfiguration esConfiguration) {
        this.restHighLevelClient = restHighLevelClient;
        this.esConfiguration = esConfiguration;
    }

    public void createConfiguration(ConfigurationLogstash configurationLogstash) {
        ISO8601DateFormat df = new ISO8601DateFormat();
        Date newDate = new Date();
        configurationLogstash.setTimestamp(df.format(newDate));
        configurationLogstash.setIdConfiguration(UUID.randomUUID().toString());
        configurationLogstash.setStatusConfig(StatusConfig.INIT);
        map.put(configurationLogstash.getIdConfiguration(), configurationLogstash);
    }

    public void deleteConfiguration(String id) {
        map.remove(id);
    }

    public void editConfiguration(ConfigurationLogstash configurationLogstash) {
        ISO8601DateFormat df = new ISO8601DateFormat();
        Date newDate = new Date();
        configurationLogstash.setTimestamp(df.format(newDate));
        map.put(configurationLogstash.getIdConfiguration(), configurationLogstash);
    }

    public List<ConfigurationLogstash> findAll() {
        return map.values().stream().collect(toList());
    }

    public ConfigurationLogstash getConfiguration(String idConfiguration) {
        return map.get(idConfiguration);
    }


    public void activeConfiguration(String idConfiguration) {
        ConfigurationLogstash cl = map.get(idConfiguration);
        if (cl != null) {
            try {
                callAddES(".logstash", cl.getName(), generateConfig(cl), cl);
                cl.setStatusConfig(StatusConfig.ACTIVE);
            } catch (Exception e) {
                log.error("Exception {}", e);
                cl.setStatusConfig(StatusConfig.ERROR);
            }
        }
    }

    public void deactiveConfiguration(String idConfiguration) {
        ConfigurationLogstash cl = map.get(idConfiguration);
        if (cl != null) {
            try {
                callRemoveES(".logstash", cl);
                cl.setStatusConfig(StatusConfig.DISABLE);
            } catch (Exception e) {
                log.error("Exception {}", e);
                cl.setStatusConfig(StatusConfig.ERROR);
            }
        }
    }

    private void callRemoveES(String index, ConfigurationLogstash cl) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, "doc", cl.getIdEs());
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
        if (deleteResponse != null) {
            deleteResponse.status();
            deleteResponse.toString();
            cl.setIdEs(null);
        }
    }

    private void callAddES(String index, String id, String valuePipeline, ConfigurationLogstash cl) throws IOException {
        BulkRequest bulk = new BulkRequest();
        String fluxJson = "{\"description\":\"\",\"username\": \"" + esConfiguration.getServiceElasticsearchUsername() + "\",\"pipeline\":\"" + StringEscapeUtils.escapeJava(valuePipeline) + "\"}";
        bulk.add(new IndexRequest(index)
                .type("doc")
                .id(id)
                .source(fluxJson, XContentType.JSON));
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulk);
        if (bulkResponse.getItems().length == 1) {
            cl.setIdEs(bulkResponse.getItems()[0].getId());
        } else {
            log.error("Problem with return ES {}", bulkResponse);
        }
    }

    public String generate(String idConfiguration) {
        ConfigurationLogstash cl = map.get(idConfiguration);
        if (cl != null) {
            return generateConfigClient(cl);
        } else {
            return "No configuration";
        }
    }

    public String generateConfigClient(ConfigurationLogstash cl) {
        StringBuilder sb = new StringBuilder();
        sb.append("# https://www.elastic.co/guide/en/logstash/current/configuring-centralized-pipelines.html");
        sb.append("# Copy into your logstash.yml");
        sb.append("xpack.management.enabled: true");
        sb.append("xpack.management.elasticsearch.url: \"" + esConfiguration.getHost() + ":" + esConfiguration.getPort() + "\"");
        sb.append("xpack.management.elasticsearch.username: " + esConfiguration.getServiceElasticsearchUsername());
        sb.append("xpack.management.elasticsearch.password: " + esConfiguration.getServiceElasticsearchPassword());
        sb.append("xpack.management.logstash.poll_interval: 5s");
        sb.append("xpack.management.pipeline.id: [\"" + cl.getName() + "\"]");
        return sb.toString();
    }

    public String generateConfig(ConfigurationLogstash cl) {
        StringBuilder sb = new StringBuilder();
        //input
        sb.append(" input {" + RETURN);
        cl.getInput().stream().forEach(e -> sb.append(generateInput(e)));
        sb.append("}" + RETURN);
        //filter
        sb.append(" filter {}" + RETURN);
        //output
        sb.append(" output {" + RETURN);
        sb.append(generateOutput(cl.getOutput()));
        sb.append("}" + RETURN);
        return sb.toString();
    }

    public String filterAction(Translate translate) {
        StringBuilder sb = new StringBuilder();
        if (translate != null && translate.getAction() != null && StringUtils.isNotBlank(translate.getAction().name())) {
            switch (translate.getAction()) {
                case DATE:
                    sb.append(" date { match => [\"" + translate.getKey() + "\", " + translate.getValue() + "]" + RETURN_BRACE);
                    break;
                case ADD:
                    sb.append(" mutate { add_field => {\"" + translate.getKey() + "\", \"" + translate.getValue() + "\"}" + RETURN_BRACE);
                    break;
                case DELETE:
                    sb.append(" mutate { remove_field => [\"" + translate.getKey() + "\"]" + RETURN_BRACE);
                    break;
                case GROK:
                    sb.append(" grok { match => {\"" + translate.getKey() + "\" => \"" + translate.getKeyPattern() + "\"}" + RETURN_BRACE);
                    break;
                case RENAME:
                    sb.append(" mutate { rename => {\"" + translate.getKey() + "\", \"" + translate.getValue() + "\"}" + RETURN_BRACE);
                    break;
                default:
                    log.error("Action not managed {}", translate);
                    throw new RuntimeException("Action not managed : " + translate.toString());
            }
        }
        return sb.toString();
    }

    public String generateFilter(Translate translate) {
        StringBuilder sb = new StringBuilder();
        if (translate.getTypeCondition() != null) {
            if (translate.getTypeCondition().getCheckPresent()) {
                if (translate.getTypeCondition().getIsPresence()) {
                    sb.append(" if [" + translate.getTypeCondition().getCondition());
                } else {
                    sb.append(" if ![" + translate.getTypeCondition().getCondition());
                }
                sb.append("] {" + RETURN);
                sb.append(filterAction(translate));
                sb.append(RETURN_BRACE);
            } else {
                if (!translate.getTypeCondition().getIsPresence()) {
                    sb.append(filterAction(translate));
                } else {
                    sb.append(" if [" + translate.getTypeCondition().getCondition() + "] {" + RETURN);
                    sb.append(filterAction(translate));
                    sb.append(RETURN_BRACE);
                }
            }
        } else {
            sb.append(filterAction(translate));
        }
        return sb.toString();
    }

    public String generateOutput(ConfigurationOutput output) {
        StringBuilder sb = new StringBuilder();
        String url = output.getPort() != null && !output.getPort().equals("") ? output.getHost() + ":" + output.getPort() : output.getHost();
        sb.append(" kafka {" + RETURN);
        sb.append(" bootstrap_servers => \"" + url + "\"" + RETURN);
        sb.append(" topic_id => \"" + output.getTopic() + "\"" + RETURN);
        if (output.getCodec() != null && StringUtils.isNotBlank(output.getCodec())) {
            sb.append(" codec => \"" + output.getCodec() + "\"" + RETURN);
        }
        sb.append(RETURN_BRACE);
        return sb.toString();
    }

    public String generateInput(ConfigurationHost input) {
        StringBuilder sb = new StringBuilder();
        switch (input.getTypeInput()) {
            case BEATS:
                sb.append(" beats { port => " + input.getPort() + RETURN);
                if (input.getTypeForced() != null) {
                    sb.append(" type => \"" + input.getTypeForced() + "\"" + RETURN_BRACE);
                } else {
                    sb.append(RETURN_BRACE);
                }
                break;
            case TCP:
                sb.append(" tcp { port => " + input.getPort() + RETURN);
                if (input.getCodec() != null && StringUtils.isNotBlank(input.getCodec())) {
                    sb.append(" codec => \"" + input.getCodec() + "\"" + RETURN);
                }
                if (input.getTypeForced() != null) {
                    sb.append(" type => \"" + input.getTypeForced() + "\"" + RETURN_BRACE);
                } else {
                    sb.append(RETURN_BRACE);
                }
                break;
            case UDP:
                sb.append(" udp { port => " + input.getPort() + RETURN);
                if (input.getCodec() != null && StringUtils.isNotBlank(input.getCodec())) {
                    sb.append(" codec => \"" + input.getCodec() + "\"" + RETURN);
                }
                if (input.getTypeForced() != null) {
                    sb.append(" type => \"" + input.getTypeForced() + "\"" + RETURN_BRACE);
                } else {
                    sb.append(RETURN_BRACE);
                }
                break;
            case FILE:
                sb.append(" file { path => \"" + input.getPath() + "\"" + RETURN);
                if (input.getTypeForced() != null) {
                    sb.append(" type => \"" + input.getTypeForced() + "\"" + RETURN_BRACE);
                } else {
                    sb.append(RETURN_BRACE);
                }
                break;
            case KAFKA:
                String url = input.getPort() != null && !input.getPort().equals("") ? input.getHost() + ":" + input.getPort() : input.getHost();
                sb.append(" kafka {" + RETURN);
                sb.append(" bootstrap_servers => \"" + url + "\"" + RETURN);
                sb.append(" topic_id => \"" + input.getTopic() + "\"" + RETURN);
                if (input.getCodec() != null && StringUtils.isNotBlank(input.getCodec())) {
                    sb.append(" codec => \"" + input.getCodec() + "\"" + RETURN);
                }
                sb.append(RETURN_BRACE);
                break;
            default:
                log.error("Type not managed {}", input);
                throw new RuntimeException("Type not managed : " + input.toString());
        }
        return sb.toString();
    }
}
