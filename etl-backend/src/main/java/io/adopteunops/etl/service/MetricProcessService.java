package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
public class MetricProcessService {

    private final RegistryService registryService;
    //should be a datastore

    public List<ConsumerState> findAll() {
        return registryService.findAll(WorkerType.METRIC_PROCESS);
    }

    public void activateProcess(ProcessMetric processMetric) {
        if (StringUtils.isBlank(processMetric.getIdProcess())) {
            processMetric = initProcessMetric(processMetric);
        }
        registryService.activate(processMetric);
    }

    private ProcessMetric initProcessMetric(ProcessMetric processMetric) {
        String idProcess = UUID.randomUUID().toString();
        ProcessMetric newProcessDefinition = processMetric.withIdProcess(idProcess);
        registryService.register(newProcessDefinition, WorkerType.METRIC_PROCESS, StatusProcess.INIT);
        return newProcessDefinition;
    }

    public void deactivateProcess(ProcessMetric processMetric) {
        registryService.deactivate(processMetric);
    }

    public ProcessDefinition findById(String id) {
        return registryService.findById(id);
    }
}
