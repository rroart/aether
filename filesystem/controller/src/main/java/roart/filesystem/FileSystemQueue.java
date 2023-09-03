package roart.filesystem;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.OperationConstants;
import roart.common.constants.QueueConstants;
import roart.common.filesystem.FileSystemFileObjectParam;
import roart.common.filesystem.FileSystemMyFileResult;
import roart.common.filesystem.FileSystemParam;
import roart.common.queue.QueueElement;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.client.HazelcastClient;

public class FileSystemQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public FileSystemQueue(String name, FileSystemAbstractController controller, CuratorFramework curatorClient, NodeConfig nodeConf) {
        HazelcastInstance hz = null;
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
            hz = HazelcastClient.newHazelcastClient();
        }
        HazelcastInstance ahz = hz;
        String ip = System.getProperty("IP");
        String fs = System.getProperty("FS");
        String path = System.getProperty("PATH");
        log.info("Using {} {} {}", ip, fs, path);
        String[] paths = path.split(",");
        for (String aPath : paths) {
            log.info("Queue name {}", QueueConstants.FS + "_" + aPath);
            final MyQueue<QueueElement> queue = new MyQueueFactory().create(QueueConstants.FS + "_" + aPath, nodeConf, curatorClient, hz);
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
                        log.info("Opid {} {}", element.getOpid(), element.getQueue());
                        if (element.getOpid().equals(OperationConstants.LISTFILESFULL)) {
                            FileSystemFileObjectParam param = element.getFileSystemFileObjectParam();
                            element.setFileSystemFileObjectParam(null);
                            FileSystemOperations operations = controller.getOperations(param);
                            try {
                                FileSystemMyFileResult ret = operations.listFilesFull(param);
                                element.setFileSystemMyFileResult(ret);
                                String queueName = element.getQueue();
                                MyQueue<QueueElement> returnQueue =  new MyQueueFactory().create(queueName, nodeConf, curatorClient, ahz);
                                returnQueue.offer(element);
                            } catch (Exception e) {
                                log.error(Constants.EXCEPTION, e); 
                            }  
                        } else {
                            log.error("Not found {}", element.getOpid());
                        }
                    }
                }
            };
            new Thread(run).start();
        }
    }
}
