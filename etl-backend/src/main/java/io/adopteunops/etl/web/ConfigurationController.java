package io.adopteunops.etl.web;

import io.adopteunops.etl.domain.ConfigurationLogstash;
import io.adopteunops.etl.service.ConfService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/configuration")
@AllArgsConstructor
public class ConfigurationController {

    private final ConfService confService;

    @ResponseStatus(OK)
    @PostMapping("createConfiguration")
    public void createConfiguration(@RequestBody ConfigurationLogstash configurationLogstash) {
        confService.createConfiguration(configurationLogstash);
    }

    @ResponseStatus(OK)
    @GetMapping("generate")
    public String generate(@PathParam("idConfiguration") String idConfiguration) {
        return confService.generate(idConfiguration);
    }

    @ResponseStatus(OK)
    @PostMapping("editConfiguration")
    public void editConfiguration(@RequestBody ConfigurationLogstash configurationLogstash) {
        confService.editConfiguration(configurationLogstash);
    }

    @ResponseStatus(OK)
    @GetMapping("findAll")
    public List<ConfigurationLogstash> findAll() {
        return confService.findAll();
    }

    @ResponseStatus(OK)
    @GetMapping("getConfiguration")
    public ConfigurationLogstash getConfiguration(@PathParam("idConfiguration") String idConfiguration) {
        return confService.getConfiguration(idConfiguration);
    }

    @ResponseStatus(OK)
    @GetMapping("deleteConfiguration")
    public List<ConfigurationLogstash> deleteConfiguration(@PathParam("idConfiguration") String idConfiguration) {
        confService.deleteConfiguration(idConfiguration);
        return findAll();
    }

    @ResponseStatus(OK)
    @GetMapping("activeConfiguration")
    public List<ConfigurationLogstash> activeConfiguration(@PathParam("idConfiguration") String idConfiguration) {
        confService.activeConfiguration(idConfiguration);
        return findAll();
    }

    @ResponseStatus(OK)
    @GetMapping("deactiveConfiguration")
    public List<ConfigurationLogstash> deactiveConfiguration(@PathParam("idConfiguration") String idConfiguration) {
        confService.deactiveConfiguration(idConfiguration);
        return findAll();
    }

}
