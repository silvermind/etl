package io.adopteunops.etl.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class GrokResultSimulate {
    public String value;
    public String jsonValue;
    public String message;
}