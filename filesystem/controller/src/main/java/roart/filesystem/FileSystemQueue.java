package roart.filesystem;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.filesystem.FileSystemFileObjectParam;
import roart.common.filesystem.FileSystemMyFileResult;
import roart.common.filesystem.FileSystemParam;
import roart.common.queue.QueueElement;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.client.HazelcastClient;

@Component
public class FileSystemQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public FileSystemQueue(String name, FileSystemAbstractController controller) {
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
        for (String aPath : paths) {
            final MyQueue<QueueElement> queue = new MyQueueFactory().create(name + "_" + aPath, nodeConf, curatorFramework, hz);
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
                        if (element.getOpid().equals("listfilesfull")) {
                            FileSystemFileObjectParam param = element.getFileSystemFileObjectParam();
                            FileSystemOperations operations = controller.getOperations(param);
                            try {
                                FileSystemMyFileResult ret = operations.listFilesFull(param);
                                element.setFileSystemMyFileResult(ret);
                            } catch (Exception e) {
                                log.error(Constants.EXCEPTION, e); 
                            }  
                        }
                    }
                }
            };
            new Thread(run).start();
        }
    }
}
