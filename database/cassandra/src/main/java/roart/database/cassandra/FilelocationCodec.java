package roart.database.cassandra;

import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import roart.common.model.FileLocation;

import java.nio.ByteBuffer;

public class FilelocationCodec implements TypeCodec<FileLocation> {

    private final TypeCodec<UdtValue> innerCodec;

    private final UserDefinedType userType;

    public FilelocationCodec(TypeCodec<UdtValue> innerCodec, Class<FileLocation> javaType) {
        this.innerCodec = innerCodec;
        this.userType = (UserDefinedType)innerCodec.getCqlType();
    }

    @Override
    public FileLocation parse(String value) {
        return value == null || value.isEmpty() || value.equalsIgnoreCase("NULL") ? 
            null : toFileLocation(innerCodec.parse(value));
    }

    @Override
    public String format(FileLocation value) {
        return value == null ? "NULL" : innerCodec.format(toUDTValue(value));
    }

    protected FileLocation toFileLocation(UdtValue value) {
        return value == null ? null : new FileLocation(
            value.getString("node"), 
            value.getString("filename")
        );
    }

    protected UdtValue toUDTValue(FileLocation value) {
        return value == null ? null : userType.newValue()
            .setString("node", value.getNode())
            .setString("filename", value.getFilename());
    }

    @Override
    public GenericType<FileLocation> getJavaType() {
        return GenericType.of(FileLocation.class);
    }

    @Override
    public DataType getCqlType() {
        return userType;
    }

    @Override
    public ByteBuffer encode(FileLocation value, ProtocolVersion protocolVersion) {
        return innerCodec.encode(toUDTValue(value), protocolVersion);
    }

    @Override
    public FileLocation decode(ByteBuffer bytes, ProtocolVersion protocolVersion) {
        return toFileLocation(innerCodec.decode(bytes, protocolVersion));
    }
}
