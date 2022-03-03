package roart.util;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import roart.common.util.JsonUtil;
import roart.common.config.Connector;
import roart.common.constants.Constants;

public class ConnectorTest {

    String s0 = "{ \"tika\" : \"eureka\", \"connection\" : \"tika.k8s\" }";
    String s = "[{ \"name\" : \"tika\", \"eureka\" : false, \"connection\" : \"tika.k8s\" }]";
    
    @Test
    public void test() {
        System.out.println(s);
        try {
            Map m1 = JsonUtil.convert(s0, Map.class);
            System.out.println("m1" + m1);
            Map m0 = convert(s0, new TypeReference<Map<String, String>>(){});
            System.out.println("m0" + m0);
            Connector[] m = JsonUtil.convert(s, Connector[].class);
            System.out.println("m" + Arrays.asList(m));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static <T> T convert(String text, TypeReference<T> typeref) {
        ObjectMapper mapper = new ObjectMapper();
        if (text != null) {
            try {
                String strippedtext = text; //strip(text);
                return mapper.convertValue(strippedtext, typeref);
            } catch (Exception e) {
                e.printStackTrace();
                //log.error(Constants.EXCEPTION, e);
            }
        }
        return null;
    }

    public static String strip(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String strippedtext = text;
        if (strippedtext.charAt(0) == '\"') {
            strippedtext = strippedtext.substring(1, strippedtext.length() - 1);
        }
        strippedtext = strippedtext.replaceAll("\\\\", "");
        if (text.length() != strippedtext.length()) {
            //log.info("Stripping json text {} to {}", text, strippedtext);
        }
        return strippedtext;
    }

}
