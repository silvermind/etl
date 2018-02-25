package io.adopteunops.etl.rules;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import static io.adopteunops.etl.rules.JSONUtils.createJsonNode;
import static org.assertj.core.api.Assertions.assertThat;

public class UtilsValidatorTest {

    @Test
    public void isGreaterThan_Ok() {
        String test = "{\"key1\": 100}";
        JsonNode JsonNode = createJsonNode(test);
        //int
        assertThat(UtilsValidator.isGreaterThan(UtilsValidator.get(JsonNode, "key1"), 50)).isTrue();
        //double
        assertThat(UtilsValidator.isGreaterThan(UtilsValidator.get(JsonNode, "key1"), 50.2)).isTrue();
    }

    @Test
    public void isGreaterThan_KO() {
        String test = "{\"key1\": 100}";
        JsonNode JsonNode = createJsonNode(test);
        assertThat(UtilsValidator.isGreaterThan(UtilsValidator.get(JsonNode, "key1"), 150)).isFalse();
        assertThat(UtilsValidator.isGreaterThan(UtilsValidator.get(JsonNode, "missingkey"), 150)).isFalse();
    }

    @Test
    public void isGreaterThanOrEqual_OK() {
        String test = "{\"key1\": 100}";
        JsonNode JsonNode = createJsonNode(test);
        assertThat(UtilsValidator.isGreaterThanOrEqual(UtilsValidator.get(JsonNode, "key1"), 100)).isTrue();
    }

    @Test
    public void isGreaterThanOrEqual_KO() {
        String test = "{\"key1\": 100}";
        JsonNode JsonNode = createJsonNode(test);
        assertThat(UtilsValidator.isGreaterThanOrEqual(UtilsValidator.get(JsonNode, "key1"), 150)).isFalse();
        assertThat(UtilsValidator.isGreaterThanOrEqual(UtilsValidator.get(JsonNode, "missingkey"), 150)).isFalse();
    }

    @Test
    public void isLowerThan_OK() {
        String test = "{\"key1\": 10}";
        JsonNode JsonNode = createJsonNode(test);
        assertThat(UtilsValidator.isLowerThan(UtilsValidator.get(JsonNode, "key1"), 50)).isTrue();
    }

    @Test
    public void isLowerThan_KO() {
        String test = "{\"key1\": 100}";
        JsonNode JsonNode = createJsonNode(test);
        assertThat(UtilsValidator.isLowerThan(UtilsValidator.get(JsonNode, "key1"), 50)).isFalse();
        //missing key
        assertThat(UtilsValidator.isLowerThan(UtilsValidator.get(JsonNode, "key2"), 150)).isFalse();

    }

    @Test
    public void isLowerThanOrEqual_OK() {
        String test = "{\"key1\": 50}";
        JsonNode JsonNode = createJsonNode(test);
        assertThat(UtilsValidator.isLowerThanOrEqual(UtilsValidator.get(JsonNode, "key1"), 50)).isTrue();
    }

    @Test
    public void isLowerThanOrEqual_KO() {
        String test = "{\"key1\": 100}";
        JsonNode JsonNode = createJsonNode(test);
        assertThat(UtilsValidator.isLowerThanOrEqual(UtilsValidator.get(JsonNode, "key1"), 50)).isFalse();
        assertThat(UtilsValidator.isLowerThanOrEqual(UtilsValidator.get(JsonNode, "missingkey"), 150)).isFalse();

    }

    @Test
    public void checkPresent_OK() {
        String test = "{\"key1\": \"blablablabla toto blablabla\", \"key2\": \"tutu\",\"key3\": \"tata\",\"key4\": \"titi\" }";
        JsonNode JsonNode = createJsonNode(test);
        assertThat(UtilsValidator.checkPresent(JsonNode, "key1", "key2", "key4")).isTrue();
    }

    @Test
    public void checkPresent_KO() {
        String test = "{\"key1\": \"blablablabla toto blablabla\", \"key2\": \"tutu\",\"key3\": \"tata\",\"key4\": \"titi\" }";
        JsonNode JsonNode = createJsonNode(test);
        assertThat(UtilsValidator.checkPresent(JsonNode, "key1", "key2", "moi")).isFalse();
    }

    @Test
    public void isDifferent() {
        String test = "{\"key1\": 50, \"key2\": \"something\"}";
        JsonNode JsonNode = createJsonNode(test);
        //number
        assertThat(UtilsValidator.isDifferentFrom(UtilsValidator.get(JsonNode, "key1"), 5)).isTrue();
        //string
        assertThat(UtilsValidator.isDifferentFrom(UtilsValidator.get(JsonNode, "key2"), "somethingelse")).isTrue();
    }

    @Test
    public void isEqual() {
        String test = "{\"key1\": 50, \"key2\": \"something\"}";
        JsonNode JsonNode = createJsonNode(test);
        //number
        assertThat(UtilsValidator.isEqualTo(UtilsValidator.get(JsonNode, "key1"), 50)).isTrue();
        //string
        assertThat(UtilsValidator.isEqualTo(UtilsValidator.get(JsonNode, "key2"), "something")).isTrue();
    }

}
