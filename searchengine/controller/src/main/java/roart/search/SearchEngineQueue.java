package roart.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.client.HazelcastClient;
import org.apache.curator.framework.CuratorFramework;
import com.hazelcast.client.config.ClientConfig;

@Component
public class SearchEngineQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public SearchEngineQueue() {
        NodeConfig nodeConf = null;
        CuratorFramework curatorFramework = null;
        HazelcastInstance hz = null;
        if (nodeConf.wantDistributedTraverse()) {
            
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.getNetworkConfig().addAddress("127.0.0.1");

            hz = HazelcastClient.newHazelcastClient(clientConfig);
        }
        new MyQueueFactory().create("", nodeConf, curatorFramework, hz);
    }
}
