package io.adopteunops.etl.admin;

import io.adopteunops.etl.config.ZookeeperConfiguration;
import io.adopteunops.etl.domain.TopicInfo;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZkUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
@AllArgsConstructor
@Slf4j
public class KafkaAdminService {

    private final ZookeeperConfiguration zookeeperConfiguration;


    public void deleteTopic(TopicInfo topicInfo) {
        ZkUtils zkUtils = zookeeperConfiguration.newConnection();
        try {
            AdminUtils.deleteTopic(zkUtils, topicInfo.getName());
            log.info("Delete topic ok {}", topicInfo.getName());
        } catch (RuntimeException e) {
            log.error("Error for delete name {} msg {}", topicInfo.getName(), e);
        } finally {
            zkUtils.close();
        }
    }

    public void createTopics(List<TopicInfo> listTopic) {
        listTopic.stream().forEach(this::createTopic);
    }

    public void buildTopic(String... topicList) {
        createTopics(Stream.of(topicList)
                .filter(e -> e != null && StringUtils.isNotBlank(e))
                .map(e -> createTopic(e))
                .collect(toList()));
    }

    private TopicInfo createTopic(String item) {
        return zookeeperConfiguration.topicDefaultValue.withName(item);
    }

    public void createTopic(TopicInfo topicInfo) {

        Properties paramTopic = new Properties();
        paramTopic.put("retention.ms", topicInfo.getRetentionHours() * 60 * 1000);

        ZkUtils zkUtils = zookeeperConfiguration.newConnection();

        try {
            AdminUtils.createTopic(zkUtils, topicInfo.getName(), topicInfo.getPartition(), topicInfo.getReplica(), paramTopic, RackAwareMode.Enforced$.MODULE$);
            log.info("Creation topic at startup ok {}", topicInfo);
        } catch (TopicExistsException e) {
            log.info("Topic {} already exist !", topicInfo.getName());
        } catch (RuntimeException e) {
            log.error("Error for create {} msg {}", topicInfo, e);
        } finally {
            zkUtils.close();
        }
    }
}