package roart.common.hcutil;

import roart.common.config.NodeConfig;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.ResultItem;

import com.hazelcast.config.Config;
import com.hazelcast.config.SerializerConfig;
import roart.common.queue.QueueElement;

public class HazelcastConfig {

    static Config getHazelcastConfig() {
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
        Config config = new Config();
        config.getSerializationConfig().addSerializerConfig(sc3);
        config.getSerializationConfig().addSerializerConfig(sc4);
        config.getSerializationConfig().addSerializerConfig(sc8);
        config.getSerializationConfig().addSerializerConfig(sc9);
        return config;
    }
}
