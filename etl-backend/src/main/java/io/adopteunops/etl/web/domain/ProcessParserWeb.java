package io.adopteunops.etl.web.domain;

import io.adopteunops.etl.domain.ProcessParser;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessParserWeb {
    private String idProcess;
    private ProcessParser processParser;
}
