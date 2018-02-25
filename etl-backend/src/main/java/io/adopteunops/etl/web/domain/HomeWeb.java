package io.adopteunops.etl.web.domain;

import io.adopteunops.etl.domain.stat.StatConfiguration;
import io.adopteunops.etl.domain.stat.StatMetric;
import io.adopteunops.etl.domain.stat.StatProcess;
import io.adopteunops.etl.domain.stat.StatWorker;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HomeWeb {
    @Builder.Default
    private Long numberProcessActive = 0L;
    @Builder.Default
    private Long numberProcessDeActive = 0L;
    @Builder.Default
    private Long numberProcessError = 0L;
    @Builder.Default
    private List<StatProcess> listStatProcess = new ArrayList<>();
    @Builder.Default
    private Long numberWorkerProcess = 0L;
    @Builder.Default
    private Long numberWorkerMetric = 0L;
    @Builder.Default
    private Long numberWorkerReferential = 0L;
    @Builder.Default
    private List<StatWorker> listStatWorker = new ArrayList<>();
    @Builder.Default
    private Long numberMetricActive = 0L;
    @Builder.Default
    private Long numberMetricDeActive = 0L;
    @Builder.Default
    private Long numberMetricError = 0L;
    @Builder.Default
    private List<StatMetric> listStatMetric = new ArrayList<>();
    @Builder.Default
    private Long numberConfigurationActive = 0L;
    @Builder.Default
    private Long numberConfigurationDeActive = 0L;
    @Builder.Default
    private Long numberConfigurationError = 0L;
    @Builder.Default
    private List<StatConfiguration> listStatConfiguration = new ArrayList<>();

}
