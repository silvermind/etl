package io.adopteunops.etl.web.domain;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DataChartsWeb {
    private DataCharts dataProcess;
    private DataCharts dataMetric;
    private DataCharts dataWorker;
    private DataCharts dataConfiguration;
}
