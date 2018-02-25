package io.adopteunops.etl.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ConfigurationLogstash {
    public String timestamp;
    @Builder.Default
    public List<ConfigurationHost> input = new ArrayList<>();
    @Builder.Default
    public ConfigurationOutput output = new ConfigurationOutput();
    public String name;
    public String idConfiguration;
    public String idEs;
    public StatusConfig statusConfig;
}
