package io.adopteunops.etl.domain;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import io.adopteunops.etl.utils.StatusCode;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class ValidateData {
    @Getter
    public boolean success;
    public StatusCode statusCode;
    @Builder.Default
    public String message = "";
    public String value;
    public JsonNode jsonValue;
    public Date timestamp;
    public TypeValidation typeValidation;
    //Mandatory
    public String type;
    //Mandatory
    public String project;
    @Builder.Default
    public List<StatusCode> errorList = new ArrayList<>();

    public ErrorData toErrorData() {
        ISO8601DateFormat df = new ISO8601DateFormat();
        return ErrorData.builder()
                .errorReason(statusCode.name())
                .errorMessage(message)
                .type(typeValidation.name())
                .message(value)
                .timestamp(df.format(new Date()))
                .build();
    }
}