package io.adopteunops.etl.web.domain;

import io.adopteunops.etl.domain.ProcessOutput;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessOutputWeb {
    private String idProcess;
    private String id;
    private ProcessOutput processOutput;
}
