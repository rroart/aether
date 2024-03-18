package roart.common.hcutil;

import roart.common.config.NodeConfig;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.ResultItem;

import com.hazelcast.config.Config;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import roart.common.queue.QueueElement;

public class HazelcastConfig {

    static void getHazelcastSerializationConfig(SerializationConfig serializationConfig) {
        /*
        SerializerConfig sc3 = new SerializerConfig()
                .setImplementation(new NodeConfigSerializer())
                .setTypeClass(NodeConfig.class);
        SerializerConfig sc4 = new SerializerConfig()
                .setImplementation(new ResultItemSerializer())
                .setTypeClass(ResultItem.class);
        SerializerConfig sc8 = new SerializerConfig()
                .setImplementation(new QueueElementSerializer())
                .setTypeClass(QueueElement.class);
        SerializerConfig sc9 = new SerializerConfig()
                .setImplementation(new InmemoryMessageSerializer())
                .setTypeClass(InmemoryMessage.class);
                */
        SerializerConfig sc3 = new SerializerConfig()
                .setImplementation(new MySerializer(NodeConfig.class, 3))
                .setTypeClass(NodeConfig.class);
        SerializerConfig sc4 = new SerializerConfig()
                .setImplementation(new MySerializer(ResultItem.class, 4))
                .setTypeClass(ResultItem.class);
        SerializerConfig sc8 = new SerializerConfig()
                .setImplementation(new MySerializer(QueueElement.class, 8))
                .setTypeClass(QueueElement.class);
        SerializerConfig sc9 = new SerializerConfig()
                .setImplementation(new MySerializer(InmemoryMessage.class, 9))
                .setTypeClass(InmemoryMessage.class);
        serializationConfig.addSerializerConfig(sc3);
        serializationConfig.addSerializerConfig(sc4);
        serializationConfig.addSerializerConfig(sc8);
        serializationConfig.addSerializerConfig(sc9);
    }
}
