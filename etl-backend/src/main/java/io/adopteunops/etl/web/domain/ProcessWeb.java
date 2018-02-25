package io.adopteunops.etl.web.domain;

import io.adopteunops.etl.domain.StatusProcess;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessWeb {
    public String idProcess;
    @Builder.Default
    public String name = "";
    @Builder.Default
    public ConfigWeb input = new ConfigWeb();
    @Builder.Default
    public ConfigWeb output = new ConfigWeb();
    @Builder.Default
    public List<ConfigWeb> validator = new ArrayList<>();
    @Builder.Default
    public List<ConfigWeb> metric = new ArrayList<>();
    public Date timestamp;
    public StatusProcess statusProcess;
}
