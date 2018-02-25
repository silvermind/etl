package io.adopteunops.etl.web;

import io.adopteunops.etl.domain.*;
import io.adopteunops.etl.service.ProcessService;
import io.adopteunops.etl.service.UtilsProcessService;
import io.adopteunops.etl.web.domain.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/process")
@AllArgsConstructor
public class ProcessController {

    private final ProcessService processService;
    private final UtilsProcessService utilsProcessService;

    @ResponseStatus(OK)
    @PostMapping("initProcess")
    public String initProcess() {
        return processService.initProcessConsumer().getIdProcess();
    }


    @ResponseStatus(OK)
    @GetMapping("addName")
    public void addName(@PathParam("idProcess") String idProcess, @PathParam("name") String name) {
        utilsProcessService.addName(name, idProcess);
    }

    @ResponseStatus(OK)
    @PostMapping("addInput")
    public void addInput(@RequestBody ProcessConsumer processConsumer) {
        utilsProcessService.addInput(processConsumer);
    }

    @ResponseStatus(OK)
    @PostMapping("addOut")
    public void addOut(@RequestBody ProcessConsumer processConsumer) {
        utilsProcessService.addOutput(processConsumer);
    }

    @ResponseStatus(OK)
    @PostMapping("createProcess")
    public void createProcess(@RequestBody ProcessConsumer processConsumer) {
        utilsProcessService.createProcess(processConsumer);
    }

    @ResponseStatus(OK)
    @GetMapping("findAll")
    public List<ConsumerState> findAll() {
        return processService.findAll();
    }

    @ResponseStatus(OK)
    @GetMapping("findConsumerState")
    public ConsumerState findConsumerState(@PathParam("idProcess") String idProcess) {
        return processService.findConsumerState(idProcess);
    }

    @ResponseStatus(OK)
    @GetMapping("findProcess")
    public ProcessConsumer findProcess(@PathParam("idProcess") String idProcess) {
        return processService.findProcess(idProcess);
    }

    @ResponseStatus(OK)
    @PostMapping("removeProcess")
    public void removeProcess(@RequestBody ProcessWeb process) {
        processService.removeProcess(process.getIdProcess());
    }

    @ResponseStatus(OK)
    @PostMapping("createProcessValidation")
    public void createProcessValidation(@RequestBody ProcessValidationWeb processValidationWeb) {
        utilsProcessService.createProcessValidation(processValidationWeb);
    }

    @ResponseStatus(OK)
    @GetMapping("findProcessValidation")
    public ProcessValidation findProcessValidation(@PathParam("idProcess") String idProcess, @PathParam("idProcessValidation") String idProcessValidation) {
        return utilsProcessService.findProcessValidation(idProcess, idProcessValidation);
    }

    @ResponseStatus(OK)
    @GetMapping("removeProcessValidation")
    public void removeProcessValidation(@PathParam("idProcess") String idProcess, @PathParam("idProcessValidation") String idProcessValidation) {
        utilsProcessService.removeProcessValidation(idProcess, idProcessValidation);
    }


    @ResponseStatus(OK)
    @GetMapping("findProcessParser")
    public ProcessParser findProcessParser(@PathParam("idProcess") String idProcess, @PathParam("id") String id) {
        return utilsProcessService.findProcessParser(idProcess, id);
    }

    @ResponseStatus(OK)
    @GetMapping("findProcessTransformation")
    public ProcessTransformation findProcessTransformation(@PathParam("idProcess") String idProcess, @PathParam("id") String id) {
        return utilsProcessService.findProcessTransformation(idProcess, id);
    }

    @ResponseStatus(OK)
    @GetMapping("findProcessFilter")
    public ProcessFilter findProcessFilter(@PathParam("idProcess") String idProcess, @PathParam("idFilter") String idFilter) {
        return utilsProcessService.findProcessFilter(idProcess, idFilter);
    }

    @ResponseStatus(OK)
    @GetMapping("removeProcessFilter")
    public void removeProcessFilter(@PathParam("idProcess") String idProcess, @PathParam("idFilter") String idFilter) {
        utilsProcessService.removeProcessFilter(idProcess, idFilter);
    }

    @ResponseStatus(OK)
    @PostMapping("createProcessFilter")
    public void createProcessFilter(@RequestBody ProcessFilterWeb processFilterWeb) {
        utilsProcessService.createProcessFilter(processFilterWeb);
    }

    @ResponseStatus(OK)
    @PostMapping("createProcessParser")
    public void createProcessParser(@RequestBody ProcessParserWeb processParserWeb) {
        utilsProcessService.createProcessParser(processParserWeb);
    }

    @ResponseStatus(OK)
    @GetMapping("removeProcessParser")
    public void removeProcessParser(@PathParam("idProcess") String idProcess, @PathParam("id") String id) {
        utilsProcessService.removeProcessParser(idProcess, id);
    }

    @ResponseStatus(OK)
    @PostMapping("createProcessTransformation")
    public void createProcessTransformation(@RequestBody ProcessTransformationWeb processTransformationWeb) {
        utilsProcessService.createProcessTransformation(processTransformationWeb);
    }

    @ResponseStatus(OK)
    @GetMapping("removeProcessTransformation")
    public void removeProcessTransformation(@PathParam("idProcess") String idProcess, @PathParam("id") String id) {
        utilsProcessService.removeProcessTransformation(idProcess, id);
    }

    @ResponseStatus(OK)
    @GetMapping("activate")
    public void activate(@PathParam("idProcess") String idProcess) throws Exception {
        processService.activateProcess(processService.findProcess(idProcess));
    }

    @ResponseStatus(OK)
    @GetMapping("deactivate")
    public void deactivate(@PathParam("idProcess") String idProcess) throws Exception {
        processService.deactivateProcess(processService.findProcess(idProcess));
    }

    @ResponseStatus(OK)
    @GetMapping("finishProcess")
    public void finishProcess(@PathParam("idProcess") String idProcess) throws Exception {
        utilsProcessService.finishProcess(idProcess);
    }


}
