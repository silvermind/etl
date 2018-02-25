package io.adopteunops.etl.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.adopteunops.etl.domain.ProcessParser;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang.StringUtils.*;
import static org.joda.time.chrono.ISOChronology.getInstanceUTC;

@Slf4j
@Component
public class CEFParser implements ParserProcess {

    public static final String CEF = "CEF:";

    public String process(String value, ProcessParser processParser) {
        ObjectMapper objectMapper = new ObjectMapper();
        String result;
        try {
            CEFEvent cef = parse(value);
            result = objectMapper.writeValueAsString(cef);
        } catch (JsonProcessingException e) {
            log.error("CEFParser JsonProcessingException {} ", e);
            result = "Parse Json Processing Exception " + e.getMessage();
        } catch (RuntimeException r) {
            log.error("CEFParser RuntimeException {} ", r);
            result = "Parse Process Exception " + r.getMessage();
        }
        return result;
    }

    public CEFEvent parse(String line) {
        line = chomp(line);
        assertFormat(line);
        int indexCef = line.indexOf(CEF);
        if (indexCef == -1) {
            throw new IllegalArgumentException("Not a CEF line: " + line);
        }

        AtomicInteger index = new AtomicInteger(indexCef);
        String cefHeader = nextField(index, line);
        if (!cefHeader.startsWith(CEF)) {
            throw new IllegalArgumentException("wrong CEF head " + cefHeader);
        }
        Map<String, String> extensions = extensions(index, line);
        DateTime timestamp = timestamp(extensions);
        return CEFEvent.builder()
                .version(cefHeader.substring(CEF.length()))
                .deviceVendor(nextField(index, line))
                .deviceProduct(nextField(index, line))
                .deviceVersion(nextField(index, line))
                .signatureId(nextField(index, line))
                .name(nextField(index, line))
                .severity(nextField(index, line))
                .extensions(extensions)
                .timestamp(timestamp)
                .build();
    }

    private DateTime timestamp(Map<String, String> extensions) {
        String timestampAsString = timestampAsString(extensions);
        if (timestampAsString == null) {
            return null;
        }
        return new DateTime(Long.valueOf(timestampAsString), getInstanceUTC());
    }

    private String timestampAsString(Map<String, String> extensions) {
        String art = extensions.get("art");
        if (art != null) {
            return art;
        }
        String rt = extensions.get("rt");
        if (rt != null) {
            return rt;
        }
        String endt = extensions.get("endt");
        if (endt != null) {
            return endt;
        }
        String st = extensions.get("st");
        if (st != null) {
            return st;
        }
        return null;
    }

    private void assertFormat(String line) {
        if (line == null) {
            throw new IllegalArgumentException("line is null");
        }
        if (line.length() == 0) {
            throw new IllegalArgumentException("line is empty");
        }
        if (isBlank(line)) {
            throw new IllegalArgumentException("line is blank");
        }
    }

    private Map<String, String> extensions(AtomicInteger index, String line) {
        Map<String, String> ret = new HashMap<>();
        String key;
        String value;
        do {
            key = nextExtension(index, line);
            if (key != null && key.length() == 0) {
                // when extra space is set after the last |
                // not in CEFParser spec, but seen in real life
                key = nextExtension(index, line);
            }
            value = nextExtension(index, line);
            if (key != null && value != null) {
                ret.put(key, value);
            }
        } while (key != null && value != null);

        return ret;
    }

    private String nextExtension(AtomicInteger start, String line) {
        if (start.get() > line.length()) {
            return null;
        }
        int end = nextExtensionToken(start, line);
        String substring = line.substring(start.intValue(), end);
        start.set(end + 1);
        // substitute first the \\ into \
        if (substring.contains("\\\\")) {
            substring = replace(substring, "\\\\", "\\");
        }
        // if we have remaining escaping, let's handle them
        if (substring.contains("\\")) {
            substring = replace(substring, "\\|", "|");
            substring = replace(substring, "\\r", "\r");
            substring = replace(substring, "\\n", "\n");
            substring = replace(substring, "\\=", "=");
        }
        return substring;
    }

    //KaN+GDsBABCAAhNGiUnojQ\=\=
    private int nextExtensionToken(AtomicInteger index, String line) {
        int lastSpace = -1;
        int length = line.length();
        for (int i = index.intValue(); i < length; i++) {
            char c = line.charAt(i);
            switch (c) {
                case ' ':
                    lastSpace = i;
                    break;
                case '=':
                    if (lastSpace != -1) {
                        return lastSpace;
                    }
                    return i;
                case '\\':
                    // sorry for that : we are trying to decode when cef producers are encoding their encoding
                    // Basically we should have \= in the stream as the spec requires
                    // Some producers send \\= we need to account for that.
                    // here is a example of the wrong encoding
                    // aid=3KaN+GDsBABCAAhNGiUnojQ\\=\\=
                    // it should have been written as
                    // aid=3KaN+GDsBABCAAhNGiUnojQ\=\=
                    if ((c + 1) <= line.length()) {
                        switch (line.charAt(i + 1)) {
                            case '\\':
                                if ((c + 2) <= line.length()) {
                                    switch (line.charAt(i + 2)) {
                                        case '\\':
                                        case '=':
                                        case '\r':
                                        case '\n':
                                            i += 2;
                                            break;
                                        default:
                                    }
                                }
                            case '=':
                            case '\r':
                            case '\n':
                                i++;
                                break;
                            default:
                                break;
                        }
                    }
                default:
                    break;
            }
        }

        return length;
    }

    private String nextField(AtomicInteger start, String line) {
        int end = nextFieldToken(start, line);
        String substring = line.substring(start.intValue(), end);
        start.set(end + 1);
        substring = replace(substring, "\\\\", "\\");
        substring = replace(substring, "\\|", "|");
        return substring;
    }

    private int nextFieldToken(AtomicInteger index, String line) {
        int length = line.length();
        for (int i = index.intValue(); i < length; i++) {
            char c = line.charAt(i);
            switch (c) {
                case '|':
                    return i;
                case '\\':
                    i++;
                    break;
                default:
                    break;
            }
        }
        return length;
    }
}
