package io.adopteunops.etl.service.parser;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.domain.ProcessParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CSVParser implements ParserProcess {

    @Override
    public String process(String value, ProcessParser processParser) {
        String[] tabKey = processParser.getSchemaCSV().split(";");
        String result;
        try {
            result = parse(value, tabKey);
        } catch (Exception e) {
            result = "CSVParser Exception " + e.getMessage();
        }
        return result;
    }

    private String parse(String value, String[] tabKey) throws Exception {
        String[] tabValue = value.split(";");
        if (tabKey.length != tabValue.length) {
            throw new Exception("Size schema " + tabKey.length + " is different from Raw " + tabValue.length);
        }
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        for (int i = 0; i < tabValue.length; i++) {
            json.put(tabKey[i], tabValue[i]);
        }
        return json.toString();
    }
}
