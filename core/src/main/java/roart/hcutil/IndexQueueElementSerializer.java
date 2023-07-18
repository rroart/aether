package roart.hcutil;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

import roart.queue.IndexQueueElement;
import roart.queue.TraverseQueueElement;

public class IndexQueueElementSerializer implements StreamSerializer<IndexQueueElement> {

  @Override
      public int getTypeId() {
      return 7;
  }

  @Override
      public void write( ObjectDataOutput out, IndexQueueElement object ) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      XMLEncoder encoder = new XMLEncoder( bos );
      encoder.writeObject( object );
      encoder.close();
      out.write( bos.toByteArray() );
      //System.out.println(bos.toString());
  }

  @Override
      public IndexQueueElement read( ObjectDataInput in ) throws IOException {
      InputStream inputStream = (InputStream) in;
      XMLDecoder decoder = new XMLDecoder( inputStream );
      return (IndexQueueElement) decoder.readObject();
  }

  @Override
      public void destroy() {
  }
}

