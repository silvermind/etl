package io.adopteunops.etl.web;

import io.adopteunops.etl.domain.*;
import io.adopteunops.etl.service.SimulateImporter;
import io.adopteunops.etl.service.SimulateResultService;
import io.adopteunops.etl.service.SimulateTextService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/manage")
@AllArgsConstructor
public class ManageController {

    private final SimulateImporter simulateImporter;
    private final SimulateResultService simulateResultService;
    private final SimulateTextService simulateTextService;

    @ResponseStatus(CREATED)
    @PostMapping("/active")
    public void simulate(@RequestBody ProcessConsumer processConsumer) {
        simulateImporter.createProcessGeneric(processConsumer);
    }

    @GetMapping("/status")
    public List<StatusConsumer> status() {
        return simulateImporter.statusExecutor();
    }

    @PostMapping("/readOutput")
    public List<SimulateData> readOutput(@RequestBody PayloadReadOutput payloadReadOutput) {
        return simulateResultService.readOutPut(payloadReadOutput.getBootStrapServers(), payloadReadOutput.getMaxPollRecords(), payloadReadOutput.getPollingTime());
    }

    @PostMapping("/readOutputFromText")
    public SimulateData readOutputFromText(@RequestBody PayloadTextForReadOutput payloadTextForReadOutput) {
        return simulateTextService.readOutputFromText(payloadTextForReadOutput.getTextSubmit(), payloadTextForReadOutput.getProcessConsumer());
    }


}
