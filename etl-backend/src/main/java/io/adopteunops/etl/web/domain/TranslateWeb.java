package io.adopteunops.etl.web.domain;

import io.adopteunops.etl.domain.Translate;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TranslateWeb {
    public String idConfiguration;
    public Translate translate;
}
