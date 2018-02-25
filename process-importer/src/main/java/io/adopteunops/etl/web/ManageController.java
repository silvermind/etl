package io.adopteunops.etl.web;

import io.adopteunops.etl.domain.ProcessConsumer;
import io.adopteunops.etl.domain.StatusConsumer;
import io.adopteunops.etl.service.ImporterGeneric;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/manage")
@AllArgsConstructor
public class ManageController {

    private final ImporterGeneric importer;

    @ResponseStatus(CREATED)
    @PostMapping("/active")
    public void createProcessGeneric(@RequestBody ProcessConsumer processConsumer) {
        importer.createProcessGeneric(processConsumer);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/disable")
    public void disable(@RequestBody ProcessConsumer processConsumer) {
        importer.disable(processConsumer);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/disableAll")
    public void disableAll() {
        importer.disableAll();
    }

    @GetMapping("/status")
    public List<StatusConsumer> status() {
        return importer.statusExecutor();
    }
}
