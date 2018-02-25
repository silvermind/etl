package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.ConsumerState;
import io.adopteunops.etl.domain.ProcessConsumer;
import io.adopteunops.etl.domain.StatusProcess;
import io.adopteunops.etl.domain.WorkerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class ProcessService {

    private final RegistryService registryService;

    public ProcessService(RegistryService registryService) {
        this.registryService = registryService;
    }

    public List<ConsumerState> findAll() {
        return registryService.findAll(WorkerType.PROCESS_CONSUMER).stream()
                .filter(consumerState -> consumerState.getStatusProcess() != StatusProcess.CREATION)
                .collect(toList());
    }

    public void activateProcess(ProcessConsumer processConsumer) throws Exception {
        registryService.activate(processConsumer);
    }

    public void deactivateProcess(ProcessConsumer processConsumer) throws Exception {
        registryService.deactivate(processConsumer);
    }

    public ProcessConsumer initProcessConsumer() {
        ProcessConsumer processConsumer = ProcessConsumer.builder().build();
        String idProcess = UUID.randomUUID().toString();
        processConsumer.setIdProcess(idProcess);
        registryService.register(processConsumer, WorkerType.PROCESS_CONSUMER, StatusProcess.CREATION);
        return processConsumer;
    }

    public void removeProcess(String idProcess) {
        registryService.remove(findProcess(idProcess));
    }

    public ProcessConsumer findProcess(String idProcess) {
        return registryService.findById(idProcess) != null ? (ProcessConsumer) registryService.findById(idProcess) : null;
    }

    public ConsumerState findConsumerState(String idProcess) {
        return registryService.findConsumerStateById(idProcess);
    }

    public void changeStatusProcess(String idProcess, StatusProcess statusProcess) {
        registryService.updateStatus(idProcess, statusProcess);
    }

}
