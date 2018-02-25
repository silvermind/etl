package io.adopteunops.etl.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PayloadReadOutput {
    private String bootStrapServers;
    private String maxPollRecords;
    private String pollingTime;
}
