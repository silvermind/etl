package io.adopteunops.etl.service.parser;

import io.adopteunops.etl.domain.GrokDomain;
import io.adopteunops.etl.domain.GrokResult;
import io.adopteunops.etl.domain.GrokResultSimulate;
import io.adopteunops.etl.domain.ProcessParser;
import io.thekraken.grok.api.Grok;
import io.thekraken.grok.api.Match;
import io.thekraken.grok.api.exception.GrokException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


@Component
@Slf4j
public class GrokService implements ParserProcess {

    private Grok grokInstance;

    @PostConstruct
    public void setup() {
        grokInstance = new Grok();
        loadAllFilePattern();
    }

    public List<GrokDomain> findGrokPatten(String filter) {
        return grokInstance.getPatterns().entrySet().stream()
                .filter(e -> filterGrok(e, filter))
                .map(e -> GrokDomain.builder()
                        .keyPattern(e.getKey())
                        .valuePattern(e.getValue())
                        .build())
                .collect(toList());
    }

    private Boolean filterGrok(Map.Entry<String, String> entry, String filter) {
        if (StringUtils.isNotBlank(filter)) {
            return entry.getKey().contains(filter);
        }
        return true;
    }

    private void loadAllFilePattern() {
        try {
            String directoryRoot = new ClassPathResource("patterns/patterns").getFile().getAbsolutePath();
            Files.list(Paths.get(directoryRoot)).forEach(e -> addGrokPattern(e));
        } catch (IOException e) {
            log.error("IOException" + e);
        }
    }

    private void addGrokPattern(Path elem) {
        log.info("loading {}", elem);
        try {
            Stream<String> stream = Files.lines(Paths.get(elem.toFile().getAbsolutePath()));
            stream.forEach(e -> loadKeyValue(e));
        } catch (IOException e) {
            log.error("IOException on load pattern file {}", elem.toFile().getAbsolutePath());
        }
    }

    private void loadKeyValue(String line) {
        String key = null;
        String value = null;
        try {
            Pattern pattern = Pattern.compile("^([A-z0-9_]+)\\s+(.*)$");
            Matcher m = pattern.matcher(line);
            if (m.matches()) {
                key = m.group(1);
                value = m.group(2);
                grokInstance.addPattern(key, value);
            }
        } catch (GrokException e) {
            log.error("GrokException for key {} and value {}", key, value);
        }
    }

    public List<GrokResult> simulateAllPattern(String input) {
        return grokInstance.getPatterns().entrySet().stream()
                .map(e -> treat(input, "%{" + e.getKey() + "}", true))
                .filter(e -> StringUtils.isBlank(e.message))
                .filter(e -> StringUtils.isNotBlank(e.value))
                .filter(e -> !e.value.equals("{}"))
                .collect(toList());
    }

    public List<GrokResultSimulate> simulate(String grokPattern, String value) {
        List<GrokResultSimulate> result = new ArrayList<>();
        if (value != null && StringUtils.isNotBlank(value)) {
            String[] tabLine = value.split("\n");
            for (String item : tabLine) {
                GrokResult resultItem = treat(item, grokPattern, true);
                result.add(GrokResultSimulate.builder()
                        .jsonValue(resultItem.value)
                        .value(item)
                        .message("{}".equals(resultItem.value) ? "No Match" : (StringUtils.isBlank(resultItem.message) ? "OK" : resultItem.message))
                        .build());
            }
        }
        return result;

    }

    @Override
    public String process(String value, ProcessParser processParser) {
        GrokResult result = treat(value, processParser.getGrokPattern(), true);
        if (StringUtils.isBlank(result.value)) {
            return result.message;
        } else {
            return result.value;
        }

    }

    private GrokResult treat(String value, String grokPattern, Boolean jsonReturn) {
        try {
            log.info("treat pattern {} for value {}", grokPattern, value);
            grokInstance.compile(grokPattern);
            Match match = grokInstance.match(value);
            match.captures();
            return GrokResult.builder().value(jsonReturn ? match.toJson() : match.toString()).pattern(grokPattern).build();
        } catch (GrokException e) {
            log.error("GrokException pattern {} message {}", grokPattern, e);
            return GrokResult.builder().message("GrokException pattern " + grokPattern + " message " + e.getMessage()).pattern(grokPattern).build();
        } catch (Exception r) {
            log.error("RuntimeException GrokService {}", r);
            GrokResult g = GrokResult.builder().message("RuntimeException GrokService  message " + r.getMessage()).pattern(grokPattern).build();
            return g;
        }
    }

}
