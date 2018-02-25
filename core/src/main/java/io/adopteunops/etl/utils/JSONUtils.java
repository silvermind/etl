package io.adopteunops.etl.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class JSONUtils {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static JSONUtils INSTANCE = new JSONUtils();

    private JSONUtils() {

    }

    public static JSONUtils getInstance() {
        return INSTANCE;
    }

    public JsonNode parse(String raw) {
        try {
            return objectMapper.readTree(raw);
        } catch (IOException e) {
            return null;
        }
    }

    public ObjectNode parseObj(String raw) {
        return (ObjectNode) parse(raw);
    }

    public <T> String asJsonString(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public <T> T parse(String raw, Class<T> destClass) {
        try {
            return objectMapper.readValue(raw, destClass);
        } catch (IOException e) {
            return null;
        }
    }


}
