package io.adopteunops.etl.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProcessValidation {
    private String id;
    private TypeValidation typeValidation;
    private ParameterValidation parameterValidation;
}
