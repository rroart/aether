package roart.database;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.OperationConstants;
import roart.common.database.DatabaseFileLocationParam;
import roart.common.database.DatabaseIndexFilesResult;
import roart.common.database.DatabaseMd5Param;
import roart.common.database.DatabaseMd5Result;

import org.springframework.stereotype.Component;
import roart.common.queue.QueueElement;

public class DatabaseQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public DatabaseQueue(String name, DatabaseAbstractController controller, CuratorFramework curatorClient, NodeConfig nodeConf) {
        final HazelcastInstance hz;
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
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
                        continue;
                    } catch (InterruptedException e) {
                        log.error(Constants.EXCEPTION, e); 
                    }                   
                } else {
                    if (element.getOpid().equals(OperationConstants.GETMD5BYFILELOCATION)) {
                        DatabaseFileLocationParam param = element.getDatabaseFileLocationParam();
                        element.setFileSystemFileObjectParam(null);
                        DatabaseOperations operations = controller.getOperation(param);
                        try {
                            DatabaseMd5Result ret = operations.getMd5ByFilelocation(param);
                            element.setDatabaseMd5Result(ret);
                            String queueName = element.getQueue();
                            MyQueue<QueueElement> returnQueue =  new MyQueueFactory().create(queueName, nodeConf, curatorClient, hz);
                            returnQueue.offer(element);
                        } catch (Exception e) {
                            log.error(Constants.EXCEPTION, e); 
                        }
                        continue;
                    }
                    if (element.getOpid().equals(OperationConstants.GETBYMD5)) {
                        DatabaseMd5Param param = element.getDatabaseMd5Param();
                        element.setFileSystemFileObjectParam(null);
                        DatabaseOperations operations = controller.getOperation(param);
                        try {
                            DatabaseIndexFilesResult ret = operations.getByMd5(param);
                            element.setDatabaseIndexFilesResult(ret);
                            String queueName = element.getQueue();
                            MyQueue<QueueElement> returnQueue =  new MyQueueFactory().create(queueName, nodeConf, curatorClient, hz);
                            returnQueue.offer(element);
                        } catch (Exception e) {
                            log.error(Constants.EXCEPTION, e); 
                        }
                        continue;
                    }
                    log.error("Not found {}", element.getOpid());
                }
            }
        };
        new Thread(run).start();
    }
}
