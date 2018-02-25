package io.adopteunops.etl.web;

import io.adopteunops.etl.domain.PayloadReadOutput;
import io.adopteunops.etl.domain.PayloadTextForReadOutput;
import io.adopteunops.etl.domain.ProcessConsumer;
import io.adopteunops.etl.domain.SimulateData;
import io.adopteunops.etl.service.ImporterService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/simulate")
@AllArgsConstructor
public class SimulateController {

    private final ImporterService importerService;

    @ResponseStatus(OK)
    @PostMapping("capture")
    public List<SimulateData> capture(@RequestBody PayloadReadOutput payloadReadOutput) {
        return importerService.capture(payloadReadOutput);
    }

    @ResponseStatus(OK)
    @PostMapping("launchSimulate")
    public void launchSimulate(@RequestBody ProcessConsumer processConsumer) {
        importerService.launchSimulate(processConsumer);
    }


    @ResponseStatus(OK)
    @PostMapping("captureFromText")
    public SimulateData captureFromText(@RequestBody PayloadTextForReadOutput payloadTextForReadOutput) {
        return importerService.captureFromText(payloadTextForReadOutput);
    }

}
