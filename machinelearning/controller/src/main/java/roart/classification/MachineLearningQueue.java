package roart.classification;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import org.springframework.stereotype.Component;

@Component
public class MachineLearningQueue {

    public MachineLearningQueue() {
        NodeConfig nodeConf = null;
        CuratorFramework curatorFramework = null;
        HazelcastInstance hz = null;
        if (nodeConf.wantDistributedTraverse()) {
            hz = HazelcastClient.newHazelcastClient();
        }
        new MyQueueFactory().create("", nodeConf, curatorFramework, hz);
    }
}
