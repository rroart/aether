package roart.hcutil;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

import roart.queue.ListQueueElement;
import roart.queue.TraverseQueueElement;

public class ListingQueueElementSerializer implements StreamSerializer<ListQueueElement> {

  @Override
      public int getTypeId() {
      return 5;
  }

  @Override
      public void write( ObjectDataOutput out, ListQueueElement object ) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      XMLEncoder encoder = new XMLEncoder( bos );
      encoder.writeObject( object );
      encoder.close();
      out.write( bos.toByteArray() );
      //System.out.println(bos.toString());
  }

  @Override
      public ListQueueElement read( ObjectDataInput in ) throws IOException {
      InputStream inputStream = (InputStream) in;
      XMLDecoder decoder = new XMLDecoder( inputStream );
      return (ListQueueElement) decoder.readObject();
  }

  @Override
      public void destroy() {
  }
}

