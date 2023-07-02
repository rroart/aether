package roart.database.cassandra;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JsonCodec<T> implements TypeCodec<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final TypeCodec<UdtValue> innerCodec;

    private final UserDefinedType userType;

    public JsonCodec(TypeCodec<UdtValue> innerCodec, Class<T> javaType) {
        this.innerCodec = innerCodec;
        this.userType = (UserDefinedType)innerCodec.getCqlType();
    }


   /*
   
    //@Override
    public ByteBuffer serialize(T value, ProtocolVersion protocolVersion) throws InvalidTypeException {
        if (value == null)
            return null;
        try {
            return ByteBuffer.wrap(objectMapper.writeValueAsBytes(value));
        } catch (JsonProcessingException e) {
            throw new InvalidTypeException(e.getMessage(), e);
        }
    }

    //@Override
    @SuppressWarnings("unchecked")
    public T deserialize(ByteBuffer bytes, ProtocolVersion protocolVersion) throws InvalidTypeException {
        if (bytes == null)
            return null;
        try {
            byte[] b = new byte[bytes.remaining()];
            // always duplicate the ByteBuffer instance before consuming it!
            bytes.duplicate().get(b);
            return (T) objectMapper.readValue(b, toJacksonJavaType());
        } catch (IOException e) {
            throw new InvalidTypeException(e.getMessage(), e);
        }
    }

    @Override
    public String format(T value) throws InvalidTypeException {
        if (value == null)
            return "NULL";
        String json;
        try {
            json = objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new InvalidTypeException(e.getMessage(), e);
        }
        return '\'' + json.replace("\'", "''") + '\'';
    }

    @Override
    @SuppressWarnings("unchecked")
    public T parse(String value) throws InvalidTypeException {
        if (value == null || value.isEmpty() || value.equalsIgnoreCase("NULL"))
            return null;
        if (value.charAt(0) != '\'' || value.charAt(value.length() - 1) != '\'')
            throw new InvalidTypeException("JSON strings must be enclosed by single quotes");
        String json = value.substring(1, value.length() - 1).replace("''", "'");
        try {
            return (T) objectMapper.readValue(json, toJacksonJavaType());
        } catch (IOException e) {
            throw new InvalidTypeException(e.getMessage(), e);
        }
    }

    protected JavaType toJacksonJavaType() {
        return TypeFactory.defaultInstance().constructType(getJavaType().getType());
    }
*/
    @Override
    public GenericType<T> getJavaType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataType getCqlType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuffer encode(T value, ProtocolVersion protocolVersion) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T decode(ByteBuffer bytes, ProtocolVersion protocolVersion) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String format(T value) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public T parse(String value) {
        // TODO Auto-generated method stub
        return null;
    }

}