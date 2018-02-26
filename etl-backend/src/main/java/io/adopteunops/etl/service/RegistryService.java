package io.adopteunops.etl.service;

import io.adopteunops.etl.config.ProcessConfiguration;
import io.adopteunops.etl.domain.*;
import io.prometheus.client.Gauge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class RegistryService {

    private final ProcessConfiguration processConfiguration;
    private HashMap<String, RegistryWorker> registeredWorkers = new HashMap<>();
    private Map<String, ConsumerState> registeredConsumers = new HashMap<>();

    public static final Gauge worker = Gauge.build()
            .name("nb_worker")
            .help("nb worker")
            .labelNames("status")
            .register();

    public RegistryService(ProcessConfiguration processConfiguration) {
        this.processConfiguration = processConfiguration;
    }

    public List<RegistryWorker> getAllStatus() {
        return registeredWorkers.values().stream().collect(toList());
    }

    // WORKERS APIs

    public void addHost(RegistryWorker registryWorker) {
        log.info("Registering {} as {}", registryWorker.getName(), registryWorker.getWorkerType());
        registeredWorkers.put(registryWorker.getName(), RegistryWorker.builder()
                .name(registryWorker.getName())
                .port(registryWorker.getPort())
                .dateRefresh(new Date())
                .ip(registryWorker.getIp())
                .workerType(registryWorker.getWorkerType())
                .status(StatusWorker.OK)
                .statusConsumerList(registryWorker.getStatusConsumerList())
                .build());
        worker.labels(StatusWorker.OK.name()).inc();
    }

    public void refresh(RegistryWorker registryWorker) {
        RegistryWorker registry = registeredWorkers.get(registryWorker.getName());
        if (registry == null) {
            log.error("Refresh but not registry for item {}", registryWorker);
        } else {
            registry.setStatus(statusWorker(registry.getDateRefresh(), registryWorker));
            registry.setDateRefresh(new Date());
            registry.setPort(registryWorker.getPort());
        }
    }

    // APIs that should be used by etl-backend

    public ConsumerState findConsumerStateById(String id) {
        if (!registeredConsumers.containsKey(id)) {
            return null;
        }
        return registeredConsumers.get(id);
    }

    public ProcessDefinition findById(String id) {
        if (!registeredConsumers.containsKey(id)) {
            return null;
        }
        return registeredConsumers.get(id).getProcessDefinition();
    }

    public List<ConsumerState> findAll(WorkerType workerType) {
        return registeredConsumers.values().stream()
                .filter(consumerState -> consumerState.getWorkerType() == workerType)
                .collect(Collectors.toList());
    }

    public void activate(ProcessDefinition processDefinition) {
        ConsumerState consumerState = registeredConsumers.get(processDefinition.getIdProcess()).withProcessDefinition(processDefinition);
        consumerState = assignConsumerToWorkers(consumerState);
        triggerAction(consumerState, "activate", StatusProcess.ENABLE, StatusProcess.ERROR);

    }

    public void remove(ProcessDefinition processDefinition) {
        deactivate(processDefinition);
        registeredConsumers.remove(processDefinition.getIdProcess());
    }

    public void deactivate(ProcessDefinition processDefinition) {
        ConsumerState consumerState = registeredConsumers.get(processDefinition.getIdProcess()).withProcessDefinition(processDefinition);
        triggerAction(consumerState, "deactivate", StatusProcess.DISABLE, StatusProcess.DISABLE);
    }

    public void register(ProcessDefinition processDefinition, WorkerType workerType, StatusProcess statusProcess) {
        ConsumerState consumerState = new ConsumerState(processDefinition, workerType, statusProcess);
        registeredConsumers.put(processDefinition.getIdProcess(), consumerState);
    }

    public void updateStatus(String idProcess, StatusProcess statusProcess) {
        ConsumerState consumerState = registeredConsumers.get(idProcess);
        registeredConsumers.put(idProcess, consumerState.withStatusProcess(statusProcess));
    }

    public void updateProcessDefinition(ProcessDefinition processDefinition) {
        ConsumerState consumerState = registeredConsumers.get(processDefinition.getIdProcess());
        registeredConsumers.put(processDefinition.getIdProcess(), consumerState.withProcessDefinition(processDefinition));
    }

    // Internal apis
    private RegistryWorker getWorkerAvailable(WorkerType workerType) throws Exception {
        return registeredWorkers.values().stream()
                .filter(e -> e.getWorkerType() == workerType)
                .filter(e -> e.getStatus() == StatusWorker.OK)
                .findFirst().orElseThrow(() -> new Exception("No Worker Available"));
    }

    private void triggerAction(ConsumerState consumerState, String action, StatusProcess statusIfOk, StatusProcess statusIfKo) {
        boolean hasErrors = consumerState.getStatusProcess() == StatusProcess.ERROR;
        for (String workerName : consumerState.getRegistryWorkers()) {
            RegistryWorker worker = registeredWorkers.get(workerName);
            log.info("triggering {} on {}", action, consumerState.getProcessDefinition());
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpEntity<ProcessDefinition> request = new HttpEntity<>(consumerState.getProcessDefinition());
                restTemplate.postForObject(worker.getBaseUrl() + "/manage/" + action, request, String.class);
            } catch (RestClientException e) {
                log.error("an error occured while triggering" + action + " on " + consumerState.getProcessDefinition(), e.getMessage());
                hasErrors = true;
            }
        }

        ConsumerState newState;
        if (!hasErrors) {
            newState = consumerState.withStatusProcess(statusIfOk);
        } else {
            newState = consumerState.withStatusProcess(statusIfKo);
        }
        registeredConsumers.put(consumerState.getId(), newState);
    }

    private ConsumerState assignConsumerToWorkers(ConsumerState consumerState) {
        StatusProcess result = StatusProcess.INIT;
        try {
            int nbWorkerToAssign = consumerState.getNbInstance() - consumerState.getRegistryWorkers().size();
            for (int i = 0; i < nbWorkerToAssign; i++) {
                consumerState.getRegistryWorkers().add(getWorkerAvailable(consumerState.getWorkerType()).getName());
            }

        } catch (Exception e) {
            log.error("No Worker available for {}", consumerState.getProcessDefinition());
            result = StatusProcess.ERROR;
        }
        ConsumerState newState = consumerState.withStatusProcess(result);
        registeredConsumers.put(consumerState.getId(), newState);

        return newState;
    }

    @Scheduled(initialDelay = 1 * 60 * 1000, fixedRate = 1 * 60 * 1000)
    public void checkWorkersAlive() {
        registeredWorkers.values().stream()
                .filter(registry -> registry.getStatus() == StatusWorker.OK)
                .forEach(registry -> registry.setStatus(statusWorker(new Date(), registry)));
        rescheduleConsumerFromDeadWorkers();
        rescheduleConsumersInError();
        worker.labels(StatusWorker.OK.name()).set(registeredWorkers.values().stream()
                .filter(registryWorker -> registryWorker.getStatus() == StatusWorker.OK)
                .count());
        worker.labels(StatusWorker.KO.name()).set(registeredWorkers.values().stream()
                .filter(registryWorker -> registryWorker.getStatus() == StatusWorker.KO)
                .count());
        worker.labels(StatusWorker.FULL.name()).set(registeredWorkers.values().stream()
                .filter(registryWorker -> registryWorker.getStatus() == StatusWorker.FULL)
                .count());
    }

    private void rescheduleConsumerFromDeadWorkers() {
        registeredWorkers.values()
                .stream()
                .filter(worker -> worker.getStatus() == StatusWorker.KO)
                .forEach(this::rescheduleConsumerFromDeadWorker);
    }

    private void rescheduleConsumerFromDeadWorker(RegistryWorker registryWorker) {
        List<StatusConsumer> consumerList = registryWorker.getStatusConsumerList();
        for (StatusConsumer statusConsumer : consumerList) {
            ConsumerState consumerState = registeredConsumers.get(statusConsumer.getIdProcessConsumer());
            //remove it from running workers
            consumerState.getRegistryWorkers().remove(registryWorker.getName());
            //elect new worker
            rescheduleProcessDefinition(consumerState);
        }
    }

    private void rescheduleProcessDefinition(ConsumerState consumerState) {
        log.info("rescheduling {}", consumerState.getProcessDefinition());
        //run it
        activate(consumerState.getProcessDefinition());
    }

    private void rescheduleConsumersInError() {
        registeredConsumers.values().stream()
                .filter(consumerState -> consumerState.getStatusProcess() == StatusProcess.ERROR)
                .forEach(this::rescheduleProcessDefinition);
    }

    private StatusWorker statusWorker(Date last, RegistryWorker registryWorker) {
        //too many consumer
        if (registryWorker.getStatusConsumerList() != null && registryWorker.getStatusConsumerList().size() > processConfiguration.getMaxProcessConsumer()) {
            return StatusWorker.FULL;
        }
        Date actual = new Date();
        LocalDateTime lActual = LocalDateTime.ofInstant(actual.toInstant(), ZoneId.systemDefault());
        LocalDateTime lastCurrent = LocalDateTime.ofInstant(last.toInstant(), ZoneId.systemDefault());
        if (lastCurrent.plusMinutes(5).plusSeconds(10).isBefore(lActual)) {
            return StatusWorker.KO;
        }
        return StatusWorker.OK;
    }
}
