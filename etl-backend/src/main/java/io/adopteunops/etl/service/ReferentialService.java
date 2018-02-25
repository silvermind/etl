package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class ReferentialService {

    private final RegistryService registryService;

    public ReferentialService(RegistryService registryService) {
        this.registryService = registryService;
    }

    public void activateProcess(ProcessReferential processReferential) throws Exception {
        registryService.activate(processReferential);
    }

    public void deactivateProcess(ProcessReferential processReferential) throws Exception {
        registryService.deactivate(processReferential);
    }

    public List<ConsumerState> findAll() {
        return registryService.findAll(WorkerType.REFERENTIAL_PROCESS).stream()
                .filter(consumerState -> consumerState.getStatusProcess() != StatusProcess.CREATION)
                .collect(toList());
    }

    public ProcessDefinition findReferential(String idReferential) {
        return registryService.findById(idReferential);
    }

    public void deleteReferential(String idReferential) {
        registryService.remove(registryService.findById(idReferential));
    }

    public ProcessReferential initProcessReferential() {
        ProcessReferential processReferential = ProcessReferential.builder().build();
        String idProcess = UUID.randomUUID().toString();
        processReferential.setIdProcess(idProcess);
        registryService.register(processReferential, WorkerType.REFERENTIAL_PROCESS, StatusProcess.CREATION);
        return processReferential;
    }

    public void addReferential(ProcessReferential processReferential) {
        processReferential.setIdProcess(UUID.randomUUID().toString());
        registryService.register(processReferential, WorkerType.REFERENTIAL_PROCESS, StatusProcess.INIT);
    }

    public void updateReferential(ProcessReferential processReferential) {
        registryService.updateProcessDefinition(processReferential);
    }
}
