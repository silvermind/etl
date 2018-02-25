package io.adopteunops.etl.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class RouteType {
    public OutType outType;
    public String topicName;
    public String type;
    public String project;
}
