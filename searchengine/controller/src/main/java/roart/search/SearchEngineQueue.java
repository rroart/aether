package roart.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.OperationConstants;
import roart.common.queue.QueueElement;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineParam;
import org.apache.zookeeper.data.Stat;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.client.HazelcastClient;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.client.config.ClientConfig;

public class SearchEngineQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public SearchEngineQueue(String name, SearchEngineAbstractController controller, CuratorFramework curatorClient, NodeConfig nodeConf) {
        final HazelcastInstance hz;
        if (nodeConf.isInmemoryServerHazelcast()) {
            hz = HazelcastClient.newHazelcastClient();
        } else {
            hz = null;
        }
        HazelcastInstance ahz = hz;
        final MyQueue<QueueElement> queue = new MyQueueFactory().create(name, nodeConf, curatorClient, hz);
        Runnable run = () -> {
            long zkTime = 0;
            while (true) {
                String path = "/" + Constants.AETHER + "/" + Constants.QUEUES + "/" + name;
                try {
                    long newTime = System.currentTimeMillis();
                    if ((newTime - zkTime) > 60 * 1000) {
                        zkTime = newTime;
                        Stat stat = curatorClient.checkExists().forPath(path);
                        if (stat == null) {
                            curatorClient.create().creatingParentsIfNeeded().forPath(path, name.getBytes());
                        } else {
                            curatorClient.setData().forPath(path, name.getBytes());
                        }
                    }
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e); 
                }
                QueueElement element = queue.poll(QueueElement.class);
                if (element == null) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        continue;
                    } catch (InterruptedException e) {
                        log.error(Constants.EXCEPTION, e); 
                    }                   
                } else {
                    log.info("Opid {} {}", element.getOpid(), element.getQueue());
                    if (element.getOpid().equals(OperationConstants.INDEX)) {
                        SearchEngineIndexParam param = element.getSearchEngineIndexParam();
                        element.setFileSystemFileObjectParam(null);
                        SearchEngineAbstractSearcher operations = controller.getSearch(param);
                        try {
                            long time = System.currentTimeMillis();
                            SearchEngineIndexResult ret = operations.indexme(param);
                            element.getIndexFiles().setTimeindex("" + (System.currentTimeMillis() - time));
                            element.setSearchEngineIndexResult(ret);
                            String queueName = element.getQueue();
                            MyQueue<QueueElement> returnQueue =  new MyQueueFactory().create(queueName, nodeConf, curatorClient, ahz);
                            returnQueue.offer(element);
                        } catch (Exception e) {
                            log.error(Constants.EXCEPTION, e); 
                        }
                        continue;
                    }
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
