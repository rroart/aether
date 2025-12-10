package roart.common.collections.util;

import tools.jackson.databind.ObjectMapper;

import roart.common.util.JsonUtil;

public class RedisUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String convert (Object o) {
        String string;
        if (o instanceof String) {
            string = (String) o;
        } else {
            string = JsonUtil.convert(o, mapper);
        }       
        return string;
    }
}
