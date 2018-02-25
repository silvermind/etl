package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.*;
import io.adopteunops.etl.web.domain.ProcessFilterWeb;
import io.adopteunops.etl.web.domain.ProcessParserWeb;
import io.adopteunops.etl.web.domain.ProcessTransformationWeb;
import io.adopteunops.etl.web.domain.ProcessValidationWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.UUID;

@Component
@Slf4j
public class UtilsProcessService {

    private final ProcessService processService;
    private final RegistryService registryService;

    public UtilsProcessService(ProcessService processService, RegistryService registryService) {
        this.processService = processService;
        this.registryService = registryService;
    }

    public void createProcessValidation(ProcessValidationWeb processValidationWeb) {
        ProcessConsumer pc = processService.findProcess(processValidationWeb.getIdProcess());
        if (processValidationWeb.getProcessValidation().getId() != null) {
            //update
            for (ProcessValidation pv : pc.getProcessValidation()) {
                if (processValidationWeb.getProcessValidation().getId().equals(pv.getId())) {
                    pv.setParameterValidation(processValidationWeb.getProcessValidation().getParameterValidation());
                    pv.setTypeValidation(processValidationWeb.getProcessValidation().getTypeValidation());
                }
            }
        } else {
            pc.getProcessValidation().add(ProcessValidation.builder()
                    .parameterValidation(processValidationWeb.getProcessValidation().getParameterValidation())
                    .typeValidation(processValidationWeb.getProcessValidation().getTypeValidation())
                    .id(UUID.randomUUID().toString())
                    .build());
        }
    }

    public void createProcessFilter(ProcessFilterWeb processFilterWeb) {
        ProcessConsumer pc = processService.findProcess(processFilterWeb.getIdProcess());
        if (processFilterWeb.getProcessFilter().getIdFilter() != null) {
            //update
            for (ProcessFilter pf : pc.getProcessFilter()) {
                if (processFilterWeb.getProcessFilter().getIdFilter().equals(pf.getIdFilter())) {
                    pf.setCriteria(processFilterWeb.getProcessFilter().getCriteria());
                    pf.setName(processFilterWeb.getProcessFilter().getName());
                }
            }
        } else {
            pc.getProcessFilter().add(ProcessFilter.builder()
                    .criteria(processFilterWeb.getProcessFilter().getCriteria())
                    .name(processFilterWeb.getProcessFilter().getName())
                    .idFilter(UUID.randomUUID().toString())
                    .build());
        }
    }

    public ProcessValidation findProcessValidation(String idProcess, String id) {
        for (ProcessValidation pv : processService.findProcess(idProcess).getProcessValidation()) {
            if (pv.getId().equals(id)) {
                return pv;
            }
        }
        return null;
    }

    public ProcessFilter findProcessFilter(String idProcess, String id) {
        for (ProcessFilter pf : processService.findProcess(idProcess).getProcessFilter()) {
            if (pf.getIdFilter().equals(id)) {
                return pf;
            }
        }
        return null;
    }

    public void createProcessTransformation(ProcessTransformationWeb processTransformationWeb) {
        ProcessConsumer pc = processService.findProcess(processTransformationWeb.getIdProcess());
        if (processTransformationWeb.getProcessTransformation().getId() != null && !processTransformationWeb.getProcessTransformation().getId().equals("")) {
            //update
            for (ProcessTransformation pt : pc.getProcessTransformation()) {
                if (processTransformationWeb.getProcessTransformation().getId().equals(pt.getId())) {
                    pt.setTypeTransformation(processTransformationWeb.getProcessTransformation().getTypeTransformation());
                    pt.setParameterTransformation(processTransformationWeb.getProcessTransformation().getParameterTransformation());
                }
            }
        } else {
            pc.getProcessTransformation().add(ProcessTransformation.builder()
                    .typeTransformation(processTransformationWeb.getProcessTransformation().getTypeTransformation())
                    .parameterTransformation(processTransformationWeb.getProcessTransformation().getParameterTransformation())
                    .id(UUID.randomUUID().toString())
                    .build());
        }
    }

    public void removeProcessParser(String idProcess, String id) {
        if (processService.findProcess(idProcess) != null) {
            processService.findProcess(idProcess).setProcessParser(ProcessParser.builder().build());
        }
    }

    public void createProcessParser(ProcessParserWeb processParserWeb) {
        ProcessConsumer pc = processService.findProcess(processParserWeb.getIdProcess());
        if (processParserWeb.getProcessParser() != null) {
            //update
            pc.getProcessParser().setGrokPattern(processParserWeb.getProcessParser().getGrokPattern());
            pc.getProcessParser().setSchemaCSV(processParserWeb.getProcessParser().getSchemaCSV());
            pc.getProcessParser().setTypeParser(processParserWeb.getProcessParser().getTypeParser());
        } else {
            pc.setProcessParser(ProcessParser.builder()
                    .typeParser(processParserWeb.getProcessParser().getTypeParser())
                    .grokPattern(processParserWeb.getProcessParser().getGrokPattern())
                    .schemaCSV(processParserWeb.getProcessParser().getSchemaCSV())
                    .id(UUID.randomUUID().toString())
                    .build());
        }
    }

    public void removeProcessTransformation(String idProcess, String id) {
        if (processService.findProcess(idProcess) != null) {
            Iterator<ProcessTransformation> iter = processService.findProcess(idProcess).getProcessTransformation().iterator();
            while (iter.hasNext()) {
                ProcessTransformation pv = iter.next();
                if (pv.getId().equals(id)) {
                    iter.remove();
                }
            }
        }
    }

    public void removeProcessValidation(String idProcess, String id) {
        if (processService.findProcess(idProcess) != null) {
            Iterator<ProcessValidation> iter = processService.findProcess(idProcess).getProcessValidation().iterator();
            while (iter.hasNext()) {
                ProcessValidation pv = iter.next();
                if (pv.getId().equals(id)) {
                    iter.remove();
                }
            }
        }
    }

    public void removeProcessFilter(String idProcess, String id) {
        if (processService.findProcess(idProcess) != null) {
            Iterator<ProcessFilter> iter = processService.findProcess(idProcess).getProcessFilter().iterator();
            while (iter.hasNext()) {
                ProcessFilter pf = iter.next();
                if (pf.getIdFilter().equals(id)) {
                    iter.remove();
                }
            }
        }
    }

    public void addInput(ProcessConsumer processConsumer) {
        processService.findProcess(processConsumer.getIdProcess()).setProcessInput(processConsumer.getProcessInput());
    }

    public void addOutput(ProcessConsumer processConsumer) {
        processService.findProcess(processConsumer.getIdProcess()).setProcessOutput(processConsumer.getProcessOutput());
    }

    public void finishProcess(String idProcess) {
        processService.changeStatusProcess(idProcess, StatusProcess.INIT);
    }

    public void createProcess(ProcessConsumer processConsumer) {
        StatusProcess status = processService.findConsumerState(processConsumer.getIdProcess()).getStatusProcess();
        registryService.updateProcessDefinition(processConsumer);
        if (status == StatusProcess.ENABLE) {
            registryService.activate(processConsumer);
        }
    }

    public void addName(String name, String idProcess) {
        registryService.updateProcessDefinition(processService.findProcess(idProcess).withName(name));
    }


    public ProcessParser findProcessParser(String idProcess, String id) {
        return processService.findProcess(idProcess).getProcessParser();
    }

    public ProcessTransformation findProcessTransformation(String idProcess, String id) {
        for (ProcessTransformation pt : processService.findProcess(idProcess).getProcessTransformation()) {
            if (pt.getId().equals(id)) {
                return pt;
            }
        }
        return null;
    }


}
