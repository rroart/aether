package roart.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;

import tools.jackson.core.type.TypeReference;

public class JsonUtilTest {

    @Test
    public void testStrip() {
        String s = "\"{\\\"a\\\":1}\""; // "{"a":1}"
        String out = JsonUtil.strip(s);
        assertEquals("{\"a\":1}", out);
    }

    @Test
    public void testConvertAndBack() throws Exception {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        String json = JsonUtil.convert(map);
        assertNotNull(json);
        LinkedHashMap result = JsonUtil.converty(json);
        assertEquals(1, result.get("a"));
    }

    @Test
    public void testConvertWithTypeReference() {
        String json = "{\"a\":2}";
        // convert to LinkedHashMap using TypeReference
        LinkedHashMap map = JsonUtil.convert(json, new TypeReference<LinkedHashMap<String,Object>>(){});
        // TODO assertEquals(2, map.get("a"));
    }
}
