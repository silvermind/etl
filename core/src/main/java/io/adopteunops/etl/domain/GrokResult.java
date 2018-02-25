package io.adopteunops.etl.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@Builder
@EqualsAndHashCode(of = "pattern")
public class GrokResult {
    @Builder.Default
    public String message = "";
    @Builder.Default
    public String value = "";
    @Builder.Default
    public String pattern = "";
}
