package io.adopteunops.etl.rules.metrics.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.streams.kstream.Windowed;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@Getter
public class MetricResult {
    private final String ruleName;
    private final String ruleDSL;
    private final String project;
    private final Map<String, Object> keys;
    private final Date startDate;
    private final Date endDate;
    private final Date timestamp;
    private final Double result;

    public MetricResult(Windowed<Keys> keysWindowed, Double result) {
        this.ruleName = keysWindowed.key().getRuleName();
        this.ruleDSL = keysWindowed.key().getRuleDSL();
        this.project =keysWindowed.key().getProject();
        this.keys = Collections.unmodifiableMap(keysWindowed.key().getKeys());
        this.startDate = new Date(keysWindowed.window().start());
        this.endDate = new Date(keysWindowed.window().end());
        //for Elasticsearch
        this.timestamp = endDate;
        this.result = result;
    }
}
