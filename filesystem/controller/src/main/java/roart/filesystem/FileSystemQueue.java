package roart.filesystem;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.client.HazelcastClient;

@Component
public class FileSystemQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public FileSystemQueue() {
        NodeConfig nodeConf = null;
        CuratorFramework curatorFramework = null;
        HazelcastInstance hz = null;
        if (nodeConf.wantDistributedTraverse()) {
            hz = HazelcastClient.newHazelcastClient();
        }
        String ip = System.getProperty("IP");
        String fs = System.getProperty("FS");
        String path = System.getProperty("PATH");
        log.info("Using {} {} {}", ip, fs, path);
        String[] paths = path.split(",");
        new MyQueueFactory().create("", nodeConf, curatorFramework, hz);
    }
}
