package roart.util;

import java.io.IOException;
import java.io.InputStream;

import tools.jackson.databind.ObjectMapper;

public interface JsonDeserializable<T> {
     static final ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    default T deserialize(String rawJson) throws IOException {
        return mapper.readValue(rawJson, (Class<T>) this.getClass());
    }
    /*
    public T <T> deSerialize(Class<T> clazz, InputStream json) {
        return mapper.readValue(json,
          mapper.getTypeFactory().constructParametricType(T.class, clazz));
    }
    */
}
