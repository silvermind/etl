package io.adopteunops.etl.web.domain;

import io.adopteunops.etl.domain.ProcessReferential;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReferentialOutputWeb {

    public String idProcess;
    public ProcessReferential referentialOutput;
}
