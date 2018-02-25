package io.adopteunops.etl.domain.stat;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class StatConfiguration {
    private String name;
    private String status;
}
