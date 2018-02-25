package io.adopteunops.etl.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ConfigurationOutput {
    public String topic;
    public String host;
    public String codec;
    public String port;
}
