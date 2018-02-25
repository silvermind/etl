package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.MetadataItem;
import io.adopteunops.etl.domain.ProcessReferential;
import io.adopteunops.etl.domain.Referential;
import io.adopteunops.etl.domain.ValidateData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.streams.processor.AbstractProcessor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ReferentialProcessor extends AbstractProcessor<String, ValidateData> {

    private final ProcessReferential processReferential;
    private final ReferentialService referentialService;

    public ReferentialProcessor(ProcessReferential processReferential, ReferentialService referentialService) {
        this.processReferential = processReferential;
        this.referentialService = referentialService;
    }

    @Override
    public void process(String key, ValidateData validateData) {
        referentialService.save(processReferential.getListAssociatedKeys().stream()
                .filter(keyTrack -> StringUtils.isNotBlank(validateData.jsonValue.path(keyTrack).asText()))
                .map(keyTrack -> createReferential(keyTrack, validateData))
                .collect(toList()));
    }

    private Referential createReferential(String keyTrack, ValidateData validateData) {
        Referential ref = Referential.builder()
                .key(processReferential.getReferentialKey())
                .value(validateData.jsonValue.path(keyTrack).asText())
                .timestamp(validateData.timestamp)
                .metadataItemSet(buildMetadata(validateData))
                .build();
        return ref;
    }

    private Set<MetadataItem> buildMetadata(ValidateData validateData) {
        return processReferential.getListMetadata().stream()
                .filter(metadata -> StringUtils.isNotBlank(validateData.jsonValue.path(metadata).asText()))
                .map(metadata -> MetadataItem.builder()
                        .key(metadata)
                        .value(validateData.jsonValue.path(metadata).asText())
                        .build())
                .collect(Collectors.toCollection(HashSet::new));
    }

}
