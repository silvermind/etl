package io.adopteunops.etl.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProcessOutput {
    @Builder.Default
    private ParameterOutput parameterOutput;
    private TypeOutput typeOutput;
    private String id;
}
