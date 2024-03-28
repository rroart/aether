package roart.classification;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.OperationConstants;
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.common.queue.QueueElement;
import roart.common.zkutil.ZKUtil;

import org.apache.zookeeper.data.Stat;

public class MachineLearningQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public MachineLearningQueue(String name, MachineLearningAbstractController controller, CuratorFramework curatorClient, NodeConfig nodeConf) {
        final MyQueue<QueueElement> queue = new MyQueueFactory().create(name, nodeConf, curatorClient);
        Runnable run = () -> {
            long zkTime = 0;
            while (true) {
                String path = ZKUtil.getPath(Constants.QUEUES) + name;
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
                    if (element.getOpid().equals(OperationConstants.CLASSIFY)) {
                        MachineLearningClassifyParam param = element.getMachineLearningClassifyParam();
                        element.setFileSystemFileObjectParam(null);
                        MachineLearningAbstractClassifier operations = controller.getClassifier(param);
                        try {
                            long time = System.currentTimeMillis();
                            MachineLearningClassifyResult ret = operations.classify(param);
                            long diff = System.currentTimeMillis() - time;
                            element.getIndexFiles().setTimeclass("" + diff);
                            log.info("classtime {} {}", element.getFileObject(), diff);
                            element.setMachineLearningClassifyResult(ret);
                            String queueName = element.getQueue();
                            MyQueue<QueueElement> returnQueue =  new MyQueueFactory().create(queueName, nodeConf, curatorClient);
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
