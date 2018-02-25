package io.adopteunops.etl.web;

import io.adopteunops.etl.generator.GeneratorErrorService;
import io.adopteunops.etl.generator.GeneratorService;
import io.adopteunops.etl.web.domain.PayloadTopic;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/generator")
@AllArgsConstructor
public class GeneratorController {

    private final GeneratorService generatorService;
    private final GeneratorErrorService generatorErrorService;

    @ResponseStatus(CREATED)
    @PutMapping("/inputTopic")
    public void inputTopic(@Valid @RequestBody PayloadTopic payload) {
        generatorService.createRandom(payload.getNbElemBySlot(), payload.getNbSlot());
    }

    @ResponseStatus(CREATED)
    @PutMapping("/errorTopic")
    public void errorTopic(@Valid @RequestBody PayloadTopic payload) {
        generatorErrorService.createRandom(payload.getNbElemBySlot());
    }
}
