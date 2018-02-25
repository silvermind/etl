package io.adopteunops.etl.web.domain;

import io.adopteunops.etl.domain.ConfigurationHost;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HostWeb {
    public String idConfiguration;
    public ConfigurationHost configurationHost;
}
