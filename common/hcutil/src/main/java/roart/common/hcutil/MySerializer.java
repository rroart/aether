package roart.common.hcutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import roart.common.util.JsonUtil;

public class MySerializer<T> implements StreamSerializer<T>  {
    private Class clazz;
    private int typeid;

    public MySerializer(Class clazz, int typeid) {
        this.clazz = clazz;
        this.typeid = typeid;
    }

    @Override
    public int getTypeId() {
        return typeid;
    }

    @Override
    public void write( ObjectDataOutput out, T object ) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //out.write( bos.toByteArray() );
        out.write( JsonUtil.convert(object).getBytes() );
        //System.out.println(bos.toString());
    }

    @Override
    public T read( ObjectDataInput in ) throws IOException {
        InputStream inputStream = (InputStream) in;
        return (T) JsonUtil.convert(new String(inputStream.readAllBytes()), clazz);
    }

    @Override
    public void destroy() {
    }

}
