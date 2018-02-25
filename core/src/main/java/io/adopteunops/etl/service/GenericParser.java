package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.ProcessConsumer;
import io.adopteunops.etl.service.parser.CEFParser;
import io.adopteunops.etl.service.parser.CSVParser;
import io.adopteunops.etl.service.parser.GrokService;
import io.adopteunops.etl.service.parser.NitroParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GenericParser {

    private final GrokService grokService;
    private final CEFParser cefParser;
    private final NitroParser nitroParser;
    private final CSVParser csvParser;

    public GenericParser(GrokService grokService, CEFParser cefParser, NitroParser nitroParser, CSVParser csvParser) {
        this.grokService = grokService;
        this.cefParser = cefParser;
        this.nitroParser = nitroParser;
        this.csvParser = csvParser;
    }

    public String apply(String value, ProcessConsumer processConsumer) {
        if (processConsumer.getProcessParser() != null && processConsumer.getProcessParser().getTypeParser() != null) {
            switch (processConsumer.getProcessParser().getTypeParser()) {
                case CEF:
                    return cefParser.process(value, processConsumer.getProcessParser());
                case NITRO:
                    return nitroParser.process(value, processConsumer.getProcessParser());
                case GROK:
                    return grokService.process(value, processConsumer.getProcessParser());
                case CSV:
                    return csvParser.process(value, processConsumer.getProcessParser());
                default:
                    log.error("Unsupported Type {}", processConsumer.getProcessParser().getTypeParser());
                    return value;
            }
        }
        return value;
    }
}
