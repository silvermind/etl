package io.adopteunops.etl.web.domain;

import io.adopteunops.etl.domain.ProcessTransformation;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessTransformationWeb {
    private String idProcess;
    private ProcessTransformation processTransformation;
}
