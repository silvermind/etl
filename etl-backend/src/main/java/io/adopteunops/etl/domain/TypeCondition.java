package io.adopteunops.etl.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TypeCondition {
    public String condition;
    public Boolean isPresence;
    public Boolean checkPresent;
}
