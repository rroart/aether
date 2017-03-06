package roart.hcutil;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

import roart.queue.TraverseQueueElement;

public class TraverseQueueElementSerializer implements StreamSerializer<TraverseQueueElement> {

  @Override
      public int getTypeId() {
      return 1;
  }

  @Override
      public void write( ObjectDataOutput out, TraverseQueueElement object ) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      XMLEncoder encoder = new XMLEncoder( bos );
      encoder.writeObject( object );
      encoder.close();
      out.write( bos.toByteArray() );
      //System.out.println(bos.toString());
  }

  @Override
      public TraverseQueueElement read( ObjectDataInput in ) throws IOException {
      InputStream inputStream = (InputStream) in;
      XMLDecoder decoder = new XMLDecoder( inputStream );
      return (TraverseQueueElement) decoder.readObject();
  }

  @Override
      public void destroy() {
  }
}

