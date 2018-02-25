package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.Referential;
import io.adopteunops.etl.repository.ReferentialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ReferentialService {

    private final ReferentialRepository referentialRepository;

    public ReferentialService(ReferentialRepository referentialRepository) {
        this.referentialRepository = referentialRepository;
    }

    public void save(List<Referential> referentialList) {
        //TODO cache is very useful
        referentialRepository.save(referentialList);
    }
}
