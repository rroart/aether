package roart.database.cassandra;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.TypeCodec;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.exceptions.InvalidTypeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import roart.common.model.FileLocation;

public class FilelocationCodec extends TypeCodec<FileLocation> {

    private final TypeCodec<UDTValue> innerCodec;

    private final UserType userType;

    public FilelocationCodec(TypeCodec<UDTValue> innerCodec, Class<FileLocation> javaType) {
        super(innerCodec.getCqlType(), javaType);
        this.innerCodec = innerCodec;
        this.userType = (UserType)innerCodec.getCqlType();
    }

    @Override
    public ByteBuffer serialize(FileLocation value, ProtocolVersion protocolVersion) throws InvalidTypeException {
        return innerCodec.serialize(toUDTValue(value), protocolVersion);
    }

    @Override
    public FileLocation deserialize(ByteBuffer bytes, ProtocolVersion protocolVersion) throws InvalidTypeException {
        return toFileLocation(innerCodec.deserialize(bytes, protocolVersion));
    }

    @Override
    public FileLocation parse(String value) throws InvalidTypeException {
        return value == null || value.isEmpty() || value.equalsIgnoreCase("NULL") ? 
            null : toFileLocation(innerCodec.parse(value));
    }

    @Override
    public String format(FileLocation value) throws InvalidTypeException {
        return value == null ? "NULL" : innerCodec.format(toUDTValue(value));
    }

    protected FileLocation toFileLocation(UDTValue value) {
        return value == null ? null : new FileLocation(
            value.getString("node"), 
            value.getString("filename")
        );
    }

    protected UDTValue toUDTValue(FileLocation value) {
        return value == null ? null : userType.newValue()
            .setString("node", value.getNode())
            .setString("filename", value.getFilename());
    }
}
