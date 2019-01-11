package roart.hcutil;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import roart.common.config.NodeConfig;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class NodeConfigSerializer implements StreamSerializer<NodeConfig> {

  @Override
      public int getTypeId() {
      return 3;
  }

  @Override
      public void write( ObjectDataOutput out, NodeConfig object ) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      XMLEncoder encoder = new XMLEncoder( bos );
      encoder.writeObject( object );
      encoder.close();
      out.write( bos.toByteArray() );
      //System.out.println(bos.toString());
  }

  @Override
      public NodeConfig read( ObjectDataInput in ) throws IOException {
      InputStream inputStream = (InputStream) in;
      XMLDecoder decoder = new XMLDecoder( inputStream );
      return (NodeConfig) decoder.readObject();
  }

  @Override
      public void destroy() {
  }
}

