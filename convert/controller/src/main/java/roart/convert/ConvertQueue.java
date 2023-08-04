package roart.convert;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Component;

import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;

@Component
public class ConvertQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ConvertQueue() {
        NodeConfig nodeConf = null;
        CuratorFramework curatorFramework = null;
        HazelcastInstance hz = null;
        if (nodeConf.wantDistributedTraverse()) {
            hz = HazelcastClient.newHazelcastClient();
        }
        new MyQueueFactory().create("", nodeConf, curatorFramework, hz);
    }
}
