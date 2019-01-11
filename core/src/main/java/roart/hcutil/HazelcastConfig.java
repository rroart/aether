package roart.hcutil;

import roart.queue.TraverseQueueElement;
import roart.common.config.NodeConfig;
import roart.common.model.FileObject;
import roart.common.model.ResultItem;

import com.hazelcast.config.Config;
import com.hazelcast.config.SerializerConfig;

public class HazelcastConfig {

    static Config getHazelcastConfig() {
        SerializerConfig sc = new SerializerConfig()
            .setImplementation(new TraverseQueueElementSerializer())
            .setTypeClass(TraverseQueueElement.class);
        SerializerConfig sc3 = new SerializerConfig()
        .setImplementation(new NodeConfigSerializer())
        .setTypeClass(NodeConfig.class);
        SerializerConfig sc4 = new SerializerConfig()
        .setImplementation(new ResultItemSerializer())
        .setTypeClass(ResultItem.class);
        Config config = new Config();
        config.getSerializationConfig().addSerializerConfig(sc);
        config.getSerializationConfig().addSerializerConfig(sc3);
        config.getSerializationConfig().addSerializerConfig(sc4);
       return config;
    }
}
