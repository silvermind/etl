package io.adopteunops.etl.web.domain;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GrokImport {

    public String path;
}
