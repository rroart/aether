package roart.convert;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Component;

import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.queue.QueueElement;

public class ConvertQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ConvertQueue(String name, ConvertAbstractController controller, CuratorFramework curatorClient, NodeConfig nodeConf) {
        final HazelcastInstance hz;
        if (nodeConf.wantDistributedTraverse()) {
            hz = HazelcastClient.newHazelcastClient();
        } else {
            hz = null;
        }
        final MyQueue<QueueElement> queue = new MyQueueFactory().create(name, nodeConf, curatorClient, hz);
        Runnable run = () -> {
            while (true) { 
                QueueElement element = queue.poll(QueueElement.class);
                if (element == null) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        return;
                    } catch (InterruptedException e) {
                        log.error(Constants.EXCEPTION, e); 
                    }                   
                } else {
                    if (element.getOpid().equals("")) {
                       continue;
                    }
                    log.error("Not found {}", element.getOpid());
                }
            }
        };
        new Thread(run).start();
    }
}
