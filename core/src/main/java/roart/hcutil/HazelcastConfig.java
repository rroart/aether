package roart.hcutil;

import roart.queue.ConvertQueueElement;
import roart.queue.IndexQueueElement;
import roart.queue.ListQueueElement;
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
        SerializerConfig sc5 = new SerializerConfig()
                .setImplementation(new ListingQueueElementSerializer())
                .setTypeClass(ListQueueElement.class);
        SerializerConfig sc6 = new SerializerConfig()
                .setImplementation(new ConvertQueueElementSerializer())
                .setTypeClass(ConvertQueueElement.class);
        SerializerConfig sc7 = new SerializerConfig()
                .setImplementation(new IndexQueueElementSerializer())
                .setTypeClass(IndexQueueElement.class);
        Config config = new Config();
        config.getSerializationConfig().addSerializerConfig(sc);
        config.getSerializationConfig().addSerializerConfig(sc3);
        config.getSerializationConfig().addSerializerConfig(sc4);
        config.getSerializationConfig().addSerializerConfig(sc5);
        config.getSerializationConfig().addSerializerConfig(sc6);
        config.getSerializationConfig().addSerializerConfig(sc7);
        return config;
    }
}
