package roart.common.hcutil;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import roart.common.model.ResultItem;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class ResultItemSerializer implements StreamSerializer<ResultItem> {

  @Override
      public int getTypeId() {
      return 4;
  }

  @Override
      public void write( ObjectDataOutput out, ResultItem object ) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      XMLEncoder encoder = new XMLEncoder( bos );
      encoder.writeObject( object );
      encoder.close();
      out.write( bos.toByteArray() );
      //System.out.println(bos.toString());
  }

  @Override
      public ResultItem read( ObjectDataInput in ) throws IOException {
      InputStream inputStream = (InputStream) in;
      try (XMLDecoder decoder = new XMLDecoder( inputStream )) {
        return (ResultItem) decoder.readObject();
    }
  }

  @Override
      public void destroy() {
  }
}

