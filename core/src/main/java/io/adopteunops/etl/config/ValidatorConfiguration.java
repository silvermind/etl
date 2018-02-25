package io.adopteunops.etl.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.temporal.TemporalUnit;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "validator")
public class ValidatorConfiguration {
    public List<String> cardinalityFieldChecks;
    public List<String> mandatoryFields;
    public List<String> blacklistType;
    public List<String> whitelistType;
    public Integer maxFields;
    public Integer maxSize;
    public Integer maximumInThePastAllowed;
    public Integer maximumInTheFutureAllowed;
    public TemporalUnit maximumInThePastAllowedUnit = DAYS;
    public TemporalUnit maximumInTheFutureAllowedUnit = DAYS;
}
