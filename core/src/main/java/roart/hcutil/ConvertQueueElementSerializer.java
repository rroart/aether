package roart.hcutil;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

import roart.queue.ConvertQueueElement;
import roart.queue.TraverseQueueElement;

public class ConvertQueueElementSerializer implements StreamSerializer<ConvertQueueElement> {

  @Override
      public int getTypeId() {
      return 6;
  }

  @Override
      public void write( ObjectDataOutput out, ConvertQueueElement object ) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      XMLEncoder encoder = new XMLEncoder( bos );
      encoder.writeObject( object );
      encoder.close();
      out.write( bos.toByteArray() );
      //System.out.println(bos.toString());
  }

  @Override
      public ConvertQueueElement read( ObjectDataInput in ) throws IOException {
      InputStream inputStream = (InputStream) in;
      XMLDecoder decoder = new XMLDecoder( inputStream );
      return (ConvertQueueElement) decoder.readObject();
  }

  @Override
      public void destroy() {
  }
}

