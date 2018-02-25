package io.adopteunops.etl.web;

import io.adopteunops.etl.domain.MetadataItem;
import io.adopteunops.etl.domain.ProcessReferential;
import io.adopteunops.etl.domain.Referential;
import io.adopteunops.etl.domain.StatusConsumer;
import io.adopteunops.etl.repository.ReferentialRepository;
import io.adopteunops.etl.service.ReferentialImporter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/manage")
@AllArgsConstructor
@Slf4j
public class ManageController {

    private final ReferentialImporter referentialImporter;
    private final ReferentialRepository referentialRepository;

    @ResponseStatus(CREATED)
    @PostMapping("/active")
    public void createProcessGeneric(@RequestBody ProcessReferential processReferential) {
        referentialImporter.activate(processReferential);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/disable")
    public void disable(@RequestBody ProcessReferential processReferential) {
        referentialImporter.deactivate(processReferential);
    }

    @GetMapping("/status")
    public List<StatusConsumer> status() {
        return referentialImporter.statusExecutor();
    }

    @GetMapping("/test")
    public void blabla() {
        List<Referential> referentialList = new ArrayList<>();
        Referential ref = Referential.builder()
                .key("1")
                .value("2")
                .build();
        Set<MetadataItem> set1 = new HashSet<>();
        set1.add(MetadataItem.builder().key("m10").value("v10").build());
        set1.add(MetadataItem.builder().key("m11").value("v11").build());
        ref.setMetadataItemSet(set1);
        Referential rnew = referentialRepository.save(ref);
        List<Referential> ll = referentialRepository.findAll();
        referentialRepository.findAll().stream().forEach(item -> log.error(item.toString()));

    }
}
