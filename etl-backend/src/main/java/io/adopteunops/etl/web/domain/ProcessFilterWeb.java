package io.adopteunops.etl.web.domain;

import io.adopteunops.etl.domain.ProcessFilter;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessFilterWeb {
    private String idProcess;
    private ProcessFilter processFilter;
}