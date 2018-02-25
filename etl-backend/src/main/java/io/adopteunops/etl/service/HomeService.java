package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.*;
import io.adopteunops.etl.domain.stat.StatConfiguration;
import io.adopteunops.etl.domain.stat.StatMetric;
import io.adopteunops.etl.domain.stat.StatProcess;
import io.adopteunops.etl.domain.stat.StatWorker;
import io.adopteunops.etl.web.domain.HomeWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class HomeService {

    private final RegistryService registryService;
    private final ConfService confService;
    private final PromService promservice;

    public HomeService(RegistryService registryService, ConfService confService, PromService promservice) {
        this.registryService = registryService;
        this.confService = confService;
        this.promservice = promservice;
    }

    public HomeWeb getHome() {
        return HomeWeb.builder()
                .numberProcessActive(registryService.findAll(WorkerType.PROCESS_CONSUMER).stream().filter(e -> e.getStatusProcess() == StatusProcess.ENABLE).count())
                .numberProcessDeActive(registryService.findAll(WorkerType.PROCESS_CONSUMER).stream().filter(e -> e.getStatusProcess() == StatusProcess.DISABLE).count())
                .numberProcessError(registryService.findAll(WorkerType.PROCESS_CONSUMER).stream().filter(e -> e.getStatusProcess() == StatusProcess.ERROR).count())
                .listStatProcess(buildListStatProcess())
                .numberMetricActive(registryService.findAll(WorkerType.PROCESS_CONSUMER).stream().filter(e -> e.getStatusProcess() == StatusProcess.ENABLE).count())
                .numberMetricDeActive(registryService.findAll(WorkerType.PROCESS_CONSUMER).stream().filter(e -> e.getStatusProcess() == StatusProcess.DISABLE).count())
                .numberMetricError(registryService.findAll(WorkerType.PROCESS_CONSUMER).stream().filter(e -> e.getStatusProcess() == StatusProcess.ERROR).count())
                .listStatMetric(buildListStatMetric())
                .numberConfigurationActive(confService.findAll().stream().filter(e -> e.statusConfig == StatusConfig.ACTIVE).count())
                .numberConfigurationDeActive(confService.findAll().stream().filter(e -> e.statusConfig == StatusConfig.DISABLE).count())
                .numberConfigurationError(confService.findAll().stream().filter(e -> e.statusConfig == StatusConfig.ERROR).count())
                .listStatConfiguration(buildListStatConfiguration())
                .numberWorkerMetric(registryService.getAllStatus().stream().filter(e -> e.getWorkerType() == WorkerType.METRIC_PROCESS).count())
                .numberWorkerProcess(registryService.getAllStatus().stream().filter(e -> e.getWorkerType() == WorkerType.PROCESS_CONSUMER).count())
                .numberWorkerProcess(registryService.getAllStatus().stream().filter(e -> e.getWorkerType() == WorkerType.REFERENTIAL_PROCESS).count())
                .listStatWorker(buildListStatWorker())
                .build();
    }

    private List<StatProcess> buildListStatProcess() {
        return registryService.findAll(WorkerType.PROCESS_CONSUMER).stream()
                .filter(e -> e.getStatusProcess() == StatusProcess.ENABLE)
                .map(e -> buildStatProcess(e))
                .collect(toList());
    }

    private StatProcess buildStatProcess(ConsumerState consumerState) {
        return StatProcess.builder()
                .name(((ProcessConsumer) consumerState.getProcessDefinition()).getName())
                .status(consumerState.getStatusProcess().name())
                .nbRead(promservice.fetchData("nb_read_kafka_count", ((ProcessConsumer) consumerState.getProcessDefinition()).getName()))
                .nbOutput(promservice.fetchData("nb_transformation_validation_count", ((ProcessConsumer) consumerState.getProcessDefinition()).getName()))
                .build();
    }

    private List<StatMetric> buildListStatMetric() {
        return registryService.findAll(WorkerType.METRIC_PROCESS).stream()
                .filter(e -> e.getStatusProcess() == StatusProcess.ENABLE)
                .map(e -> buildStatMetric(e))
                .collect(toList());
    }

    private StatMetric buildStatMetric(ConsumerState consumerState) {
        return StatMetric.builder()
                .name(((ProcessMetric) consumerState.getProcessDefinition()).getName())
                .status(consumerState.getStatusProcess().name())
                .nbMetric(0L)
                .build();
    }

    private List<StatConfiguration> buildListStatConfiguration() {
        return confService.findAll().stream()
                .map(e -> buildStatConfiguration(e))
                .collect(toList());
    }

    private StatConfiguration buildStatConfiguration(ConfigurationLogstash configurationLogstash) {
        return StatConfiguration.builder()
                .name(configurationLogstash.getName())
                .status(configurationLogstash.getStatusConfig().name())
                .build();
    }

    private List<StatWorker> buildListStatWorker() {
        return registryService.getAllStatus().stream()
                .map(e -> buildStatWorker(e))
                .collect(toList());
    }

    private StatWorker buildStatWorker(RegistryWorker registryWorker) {
        return StatWorker.builder()
                .name(registryWorker.getName())
                .ip(registryWorker.getIp())
                .nbProcess(Long.valueOf(registryWorker.getStatusConsumerList().size()))
                .type(registryWorker.getWorkerType().name())
                .build();
    }
}
