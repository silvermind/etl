package io.adopteunops.etl.service.validate;

import com.fasterxml.jackson.databind.JsonNode;
import io.adopteunops.etl.domain.ProcessKeyValue;
import io.adopteunops.etl.domain.ProcessValidation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.utils.StatusCode;
import io.prometheus.client.Counter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static io.adopteunops.etl.service.UtilsValidateData.createValidateData;
import static java.util.stream.Collectors.toList;

@Slf4j
public class BlackListValidator extends ValidatorProcess {

    public BlackListValidator(TypeValidation type) {
        super(type);
    }

    private static final Counter nbMessageBlackList = Counter.build()
            .name("nb_message_blacklist")
            .help("nb message blacklist")
            .labelNames("fieldname")
            .register();

    public ValidateData process(ProcessValidation processValidation, JsonNode jsonValue, String value) {

        if (processValidation.getParameterValidation().getBlackList() != null) {
            List<ProcessKeyValue> listBlack = processValidation.getParameterValidation().getBlackList()
                    .stream()
                    .filter(e -> isBlackList(jsonValue, e))
                    .collect(toList());
            if (!listBlack.isEmpty()) {
                listBlack.forEach(item -> nbMessageBlackList.labels(item.getKey()+"-"+item.getValue()).inc());
                return createValidateData(false, StatusCode.blacklist, TypeValidation.BLACK_LIST_FIELD, value, listBlack.stream().map(e -> e.getKey() + "-" + e.getValue()).collect(Collectors.joining(";")));
            } else {
                return ValidateData.builder()
                        .success(true)
                        .typeValidation(TypeValidation.BLACK_LIST_FIELD)
                        .jsonValue(jsonValue)
                        .build();
            }
        } else {
            nbMessageBlackList.labels("empty").inc();
            return createValidateData(false, StatusCode.blacklist, TypeValidation.BLACK_LIST_FIELD, value, "Blacklist array is null");
        }
    }


    private Boolean isBlackList(JsonNode jsonValue, ProcessKeyValue processKeyValue) {
        return jsonValue.path(processKeyValue.getKey()).asText().equals(processKeyValue.getValue());
    }
}
