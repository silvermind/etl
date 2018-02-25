package io.adopteunops.etl.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProcessKeyValue {
    private String key;
    private String value;
}
