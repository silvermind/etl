package io.adopteunops.etl.domain;

import lombok.*;
import lombok.experimental.Wither;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Wither
@Builder
@ToString
@EqualsAndHashCode(of = "idMetric")
public class ProcessMetric implements ProcessDefinition {
    private String idProcess;
    private String name;
    private String aggFunction;
    private String fromTopic;
    private String where;
    private String groupBy;

    private WindowType windowType;
    private long size;
    private TimeUnit sizeUnit;
    private long advanceBy;
    private TimeUnit advanceByUnit;

    private TypeOutput typeOutput;
    private String toTopic;
    private RetentionLevel retentionLevel;


    private Date timestamp;

    public String toDSL() {
        String dsl = "SELECT " +
                aggFunction +
                " FROM " + fromTopic;

        dsl += toWindowDSL();
        if (StringUtils.isNotBlank(where)) {
            dsl += " WHERE " + where;
        }

        if (StringUtils.isNotBlank(groupBy)) {
            dsl += " GROUP BY " + groupBy;
        }
        dsl += toDestination();
        return dsl;
    }

    private String toDestination() {
        switch (typeOutput) {
            case SYSTEM_OUT:
                return " TO " + typeOutput;
            case KAFKA:
                return " TO " + typeOutput + toTopic;
            case ELASTICSEARCH:
                return " TO " + typeOutput + retentionLevel;
            default:
                throw new IllegalArgumentException("Unsupported output type" + typeOutput);
        }

    }

    private String toWindowDSL() {
        switch (windowType) {
            case HOPPING:
                return " WINDOW " + windowType + '(' + size + sizeUnit + ',' + advanceBy + advanceByUnit + ')';
            case SESSION:
            case TUMBLING:
                return " WINDOW " + windowType + '(' + size + sizeUnit + ')';
            default:
                throw new IllegalArgumentException("Unsupported window type " + windowType);
        }
    }

}
