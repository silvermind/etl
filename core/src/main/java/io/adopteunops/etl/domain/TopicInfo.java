package io.adopteunops.etl.domain;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TopicInfo {
    private String name;
    private Integer partition;
    private Integer replica;
    private Boolean secure;
    private Integer retentionHours;
    private Integer sessionTimeoutSec;
    private Integer connectionTimeoutSec;
    private String bootstrapServers;
}
