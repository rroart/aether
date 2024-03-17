package roart.common.hcutil;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import roart.common.queue.QueueElement;

public class QueueElementSerializer implements StreamSerializer<QueueElement>  {
    @Override
    public int getTypeId() {
    return 8;
}

@Override
    public void write( ObjectDataOutput out, QueueElement object ) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XMLEncoder encoder = new XMLEncoder( bos );
    encoder.writeObject( object );
    encoder.close();
    out.write( bos.toByteArray() );
    //System.out.println(bos.toString());
}

@Override
    public QueueElement read( ObjectDataInput in ) throws IOException {
    InputStream inputStream = (InputStream) in;
    try (XMLDecoder decoder = new XMLDecoder( inputStream )) {
      return (QueueElement) decoder.readObject();
  }
}

@Override
    public void destroy() {
}

}
