package roart.common.util;

import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;

import roart.common.constants.Constants;
import roart.common.util.JsonUtil.JsonResponse;

public class JsonUtil {

    private static Logger log = LoggerFactory.getLogger(JsonUtil.class);

    public static <T> T convert(String text, TypeReference<T> typeref) {
        ObjectMapper mapper = new ObjectMapper();
        if (text != null) {
            try {
                String strippedtext = strip(text);
                return mapper.convertValue(strippedtext, typeref);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        return null;
    }

    public static <T> T convertnostrip(String text, Class<T> myclass) {
        if (myclass == String.class) {
            return (T) text;
        }
        ObjectMapper mapper = new ObjectMapper();
        if (text != null) {
            try {
                return mapper.readValue(text, myclass);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        return null;
    }

    public static <T> T convert(String text, Class<T> myclass) {
        ObjectMapper mapper = new ObjectMapper();
        if (text != null) {
            try {
                String strippedtext = strip(text);
                return mapper.readValue(strippedtext, myclass);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        return null;
    }

    public static <T> T convert(String text, Class<T> myclass, ObjectMapper mapper) {
        if (text != null) {
            try {
                String strippedtext = strip(text);
                return mapper.readValue(strippedtext, myclass);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
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
            log.info("Stripping json text {} to {}", text, strippedtext);
        }
        return strippedtext;
    }

    public static String convert(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        if (object != null) {
            try {
                return mapper.writeValueAsString(object);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        return null;
    }

    public static String convert(Object object, ObjectMapper mapper) {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        if (object != null) {
            try {
                return mapper.writeValueAsString(object);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        return null;
    }

    public static <T> T convert(LinkedHashMap map, Class<T> myclass) {
        ObjectMapper mapper = new ObjectMapper();
        if (map != null) {
            try {
                return mapper.convertValue(map, myclass);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        return null;
    }

    public static <T> T convert(LinkedHashMap[] map, Class<T> myclass) {
        ObjectMapper mapper = new ObjectMapper();
        if (map != null) {
            try {
                return mapper.convertValue(map, myclass);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        return null;
    }

    public static <T> T copy(Object object) {
        String text = convert(object);
        return (T) convert(text, object.getClass());
    }

    public static <T> T convertx(String string, Class<T> cls) {
        //JsonUtil.convert(string, T.class);
        return JsonUtil.convert(string, cls);
    }

    /**
     * converts to LinkedHashMap
     * @param <T>
     * @param string
     * @return
     * @throws Exception
     */
    public static <T> T converty(String string) throws Exception {
        //JsonUtil.convert(string, T.class);
        return new ObjectMapper().readValue(string, new TypeReference<>() {});
    }

    public static <T> T convertz(String string) throws Exception {
        //JsonUtil.convert(string, T.class);
        return new ObjectMapper().readValue(string, new TypeReference<T>() {});
    }

    public static <T> JsonData<T> convertx(String string) throws Exception {
        //JsonUtil.convert(string, T.class);
        return new ObjectMapper().readValue(string, new TypeReference<JsonData<T>>() {});
    }

    public static <T> JsonData<T> convertxx(String string) throws Exception {
        //JsonUtil.convert(string, T.class);
        JavaType type = new ObjectMapper().getTypeFactory().constructParametricType(JsonData.class, String.class);
        return new ObjectMapper().readValue(string, type);
    }

    public static <T> T convertJsonToPOJO(String filePath, Class<?> target) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue((filePath), objectMapper .getTypeFactory().constructParametricType(JsonResponse.class, Class.forName(target.getName())));
    }
    
    public class JsonResponse<T> {
        public JsonResponse() {
            super();
        }

        // getters and setters...
    }
}
