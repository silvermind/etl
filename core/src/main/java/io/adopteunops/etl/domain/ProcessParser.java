package io.adopteunops.etl.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProcessParser {
    private TypeParser typeParser;
    private String grokPattern;
    private String schemaCSV;
    private String id;

}
