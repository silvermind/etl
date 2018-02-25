package io.adopteunops.etl.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ErrorData {
    @JsonProperty("@timestamp")
    public String timestamp;
    public String errorReason;
    public String errorMessage;
    public String type;
    public String message;
}


