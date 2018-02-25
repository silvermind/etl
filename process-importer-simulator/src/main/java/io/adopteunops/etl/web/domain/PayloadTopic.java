package io.adopteunops.etl.web.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PayloadTopic {
    private Integer nbElemBySlot;
    private Integer nbSlot;
}
