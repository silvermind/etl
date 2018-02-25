package io.adopteunops.etl.web;

import io.adopteunops.etl.service.RetryImporter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/manage")
@AllArgsConstructor
public class ManageController {

    private final RetryImporter retryImporter;

    @ResponseStatus(OK)
    @GetMapping("/active")
    public void errorImporter() {
        retryImporter.enable();
    }

    @ResponseStatus(OK)
    @GetMapping("/disable")
    public void disable() {
        retryImporter.disable();
    }

}
