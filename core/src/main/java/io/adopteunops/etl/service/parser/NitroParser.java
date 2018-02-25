package io.adopteunops.etl.service.parser;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.domain.ProcessParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class NitroParser implements ParserProcess {
    private final static String NITRO = "McAfeeWG";

    @Override
    public String process(String value, ProcessParser processParser) {
        String result;
        try {
            checkLineNitro(value);
            JsonNode jsonResult = parse(value);
            return jsonResult.toString();
        } catch (Exception e) {
            log.error("Nitro Parser {}", e);
            result = "Parse Process Nitro Exception " + e.getMessage();
        }
        return result;
    }

    private JsonNode parse(String value) throws Exception {
        String nitro = value.substring(NITRO.length());
        Pattern pattern = Pattern.compile("[^|]*?=[^|]*");
        Matcher m = pattern.matcher(nitro);
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        while (m.find()) {
            String[] arraySplit = m.group().split("=");
            try {
                if (arraySplit.length > 1 && StringUtils.isNotBlank(arraySplit[0]) && arraySplit.length <= 2) {
                    String keyItem = arraySplit[0];
                    String valueItem = arraySplit[1];
                    json.put(keyItem, valueItem);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("ArrayIndexOutOfBoundsException {}", e);
                throw new Exception("ArrayIndexOutOfBoundsException with Parse method {}", e);
            }
        }
        //if (json.length() == 0) {
        //    throw new Exception("Nitro Parser 0 out for value : " + value);
        //}
        return json;
    }

    private void checkLineNitro(String value) throws Exception {
        if (value == null) {
            throw new Exception("Nitro line null");
        }

        if (value.length() == 0) {
            throw new Exception("Nitro line empty");
        }

        if (StringUtils.isBlank(value)) {
            throw new Exception("Nitro line blank");
        }

        int indexNitro = value.indexOf(NITRO);
        if (indexNitro == -1) {
            throw new Exception("Nitro line not good format, doesn't start with " + NITRO + " value : " + value);
        }
    }
}
