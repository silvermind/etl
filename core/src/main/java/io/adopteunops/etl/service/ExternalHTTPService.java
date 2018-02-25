package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.ProcessConsumer;
import io.adopteunops.etl.domain.ProcessKeyValue;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.domain.WorkerHTTPService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

@AllArgsConstructor
@Component
@Slf4j
public class ExternalHTTPService {
    @Getter
    private final HashMap<String, WorkerHTTPService> mapExternalService = new HashMap<>();

    public void buildCache(ProcessConsumer processConsumer) {
        if (mapExternalService.get(processConsumer.getIdProcess()) != null) {
            log.error("Build Cache exist for {}", processConsumer);
        } else {
            LocalDateTime lInit = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).minusHours(1);
            processConsumer.getProcessTransformation().stream()
                    .filter(process -> process.getTypeTransformation() == TypeValidation.LOOKUP_EXTERNAL)
                    .forEach(process -> mapExternalService.put(processConsumer.getIdProcess(), WorkerHTTPService.builder()
                            .externalHTTPService(process.getParameterTransformation().getExternalHTTPData())
                            .lastRefresh(lInit)
                            .build()));
            refresh();
        }
    }

    public void revokeCache(ProcessConsumer processConsumer) {
        mapExternalService.remove(processConsumer.getIdProcess());
    }

    @Scheduled(initialDelay = 20 * 1000, fixedRate = 10 * 1000)
    public void refresh() {
        LocalDateTime now = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        mapExternalService.values().stream()
                .filter(e -> e.getLastRefresh().plusSeconds(e.getExternalHTTPService().getRefresh()).isBefore(now))
                .forEach(e -> refreshData(e, now));
    }

    private void refreshData(WorkerHTTPService workerHTTPService, LocalDateTime now) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity request = workerHTTPService.getExternalHTTPService().getHttpMethod() == HttpMethod.POST ? new HttpEntity<String>(workerHTTPService.getExternalHTTPService().getBody()) : null;
        try {
            ResponseEntity<ProcessKeyValue[]> responseEntity = restTemplate.exchange(workerHTTPService.getExternalHTTPService().getUrl(),
                    workerHTTPService.getExternalHTTPService().getHttpMethod(),
                    request,
                    ProcessKeyValue[].class);
            if (responseEntity.getBody() != null && responseEntity.getBody().length > 0) {
                workerHTTPService.getMapResult().clear();
                for (ProcessKeyValue pKV : responseEntity.getBody()) {
                    workerHTTPService.getMapResult().put(pKV.getKey(), pKV.getValue());
                }
                workerHTTPService.setLastRefresh(now);
            }
        } catch (Exception e) {
            log.error("Error during call to {}", workerHTTPService.getExternalHTTPService(), e.getMessage());
        }
    }

}
























