package io.adopteunops.etl.web.domain;

import io.adopteunops.etl.domain.ProcessValidation;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessValidationWeb {
    private String idProcess;
    private ProcessValidation processValidation;
}