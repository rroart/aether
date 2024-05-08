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
import roart.common.filesystem.FileSystemMessageResult;
import roart.common.filesystem.FileSystemMyFileResult;
import roart.common.filesystem.FileSystemParam;
import roart.common.filesystem.FileSystemStringResult;
import roart.common.queue.QueueElement;
import roart.common.zkutil.ZKUtil;

import org.apache.zookeeper.data.Stat;

public class FileSystemQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public FileSystemQueue(String name, FileSystemAbstractController controller, CuratorFramework curatorClient, NodeConfig nodeConf) {
        String appid = System.getenv(Constants.APPID) != null ? System.getenv(Constants.APPID) : "";
        String ip = System.getProperty("IP");
        String fs = System.getProperty("FS");
        String mypath = System.getProperty("PATH");
        log.info("Using {} {} {}", ip, fs, mypath);
        String[] paths = mypath.split(",");
        for (String aPath : paths) {
            log.info("Queue name {}", QueueConstants.FS + "_" + aPath + appid);
            final String aName = QueueConstants.FS + "_" + aPath + appid;
            final MyQueue<QueueElement> queue = new MyQueueFactory().create(QueueConstants.FS + "_" + aPath + appid, nodeConf, curatorClient);
            Runnable run = () -> {
                long zkTime = 0;
                while (true) {
                    String path = ZKUtil.getAppidPath(Constants.QUEUES) + aName;
                    try {
                        long newTime = System.currentTimeMillis();
                        if ((newTime - zkTime) > 60 * 1000) {
                            zkTime = newTime;
                            Stat stat = curatorClient.checkExists().forPath(path);
                            // TODO fix name
                            if (stat == null) {
                                curatorClient.create().creatingParentsIfNeeded().forPath(path, name.getBytes());
                            } else {
                                curatorClient.setData().forPath(path, name.getBytes());
                            }
                        }
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e); 
                    }
                    QueueElement element = null; 
                    try {
                        element = queue.poll(QueueElement.class);
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e); 
                    }
                    if (element == null) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            continue;
                        } catch (InterruptedException e) {
                            log.error(Constants.EXCEPTION, e); 
                        }                   
                    } else {
                        log.info("Opid {} {} {}", element.getOpid(), element.getQueue(), element.getMd5());
                        if (element.getOpid().equals(OperationConstants.LISTFILESFULL)) {
                            FileSystemFileObjectParam param = element.getFileSystemFileObjectParam();
                            element.setFileSystemFileObjectParam(null);
                            FileSystemOperations operations = controller.getOperations(param);
                            try {
                                FileSystemMyFileResult ret = operations.listFilesFull(param);
                                element.setFileSystemMyFileResult(ret);
                                String queueName = element.getQueue();
                                MyQueue<QueueElement> returnQueue =  new MyQueueFactory().create(queueName, nodeConf, curatorClient);
                                returnQueue.offer(element);
                            } catch (Exception e) {
                                log.error(Constants.EXCEPTION, e); 
                            }
                            continue;
                        }
                        if (element.getOpid().equals(OperationConstants.GETMD5)) {
                            FileSystemFileObjectParam param = element.getFileSystemFileObjectParam();
                            element.setFileSystemFileObjectParam(null);
                            FileSystemOperations operations = controller.getOperations(param);
                            try {
                                FileSystemStringResult ret = operations.getMd5(param);
                                element.setFileSystemStringResult(ret);
                                String queueName = element.getQueue();
                                MyQueue<QueueElement> returnQueue =  new MyQueueFactory().create(queueName, nodeConf, curatorClient);
                                returnQueue.offer(element);
                            } catch (Exception e) {
                                log.error(Constants.EXCEPTION, e); 
                            }
                            continue;
                        }
                        if (element.getOpid().equals(OperationConstants.READFILE)) {
                            FileSystemFileObjectParam param = element.getFileSystemFileObjectParam();
                            element.setFileSystemFileObjectParam(null);
                            FileSystemOperations operations = controller.getOperations(param);
                            try {
                                FileSystemMessageResult ret = operations.readFile(param);
                                element.setFileSystemMessageResult(ret);
                                String queueName = element.getQueue();
                                MyQueue<QueueElement> returnQueue =  new MyQueueFactory().create(queueName, nodeConf, curatorClient);
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
}
