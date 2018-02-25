package io.adopteunops.etl.web;

import io.adopteunops.etl.domain.ProcessMetric;
import io.adopteunops.etl.service.MetricImporter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/manage")
@AllArgsConstructor
public class ManageController {

    private final MetricImporter metricImporter;

    @ResponseStatus(CREATED)
    @PostMapping("/activate")
    public void activate(@RequestBody ProcessMetric processMetric) {
        metricImporter.activate(processMetric);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/deactivate")
    public void deactivate(@RequestBody ProcessMetric processMetric) {
        metricImporter.deactivate(processMetric);
    }


}
