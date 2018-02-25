package io.adopteunops.etl.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ParameterOutput {
    @Builder.Default
    private String topicOut = "";
    @Builder.Default
    private RetentionLevel elasticsearchRetentionLevel = RetentionLevel.week;
}
