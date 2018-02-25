package io.adopteunops.etl.config;

import io.adopteunops.etl.domain.TopicInfo;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import lombok.Getter;
import lombok.Setter;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperConfiguration {
    public String bootstrapServers = "localhost:2181";
    public Integer sessionTimeoutSec = 10;
    public Integer connectionTimeoutSec = 8;
    public TopicInfo topicDefaultValue = TopicInfo.builder()
            .partition(2)
            .replica(1)
            .secure(false)
            .retentionHours(8)
            .build();

    public ZkUtils newConnection() {
        int sessionTimeoutMs = sessionTimeoutSec * 1000;
        int connectionTimeoutMs = connectionTimeoutSec * 1000;
        String zookeeperConnect = bootstrapServers;

        ZkClient zkClient = new ZkClient(
                zookeeperConnect,
                sessionTimeoutMs,
                connectionTimeoutMs,
                ZKStringSerializer$.MODULE$);
        return new ZkUtils(zkClient, new ZkConnection(zookeeperConnect), topicDefaultValue.getSecure());
    }
}
