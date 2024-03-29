package roart.convert;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.Converter;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.OperationConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.queue.QueueElement;
import roart.common.zkutil.ZKUtil;

public class ConvertQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ConvertQueue(String name, ConvertAbstractController controller, CuratorFramework curatorClient, NodeConfig nodeConf) {
        final MyQueue<QueueElement> queue = new MyQueueFactory().create(name, nodeConf, curatorClient);
        Runnable run = () -> {
            long zkTime = 0;
            while (true) { 
                String path = ZKUtil.getAppidPath(Constants.QUEUES) + name;
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
                    if (element.getOpid().equals(OperationConstants.CONVERT)) {
                        ConvertParam param = element.getConvertParam();
                        element.setFileSystemFileObjectParam(null);
                        ConvertAbstract operations = controller.getConvert(param);
                        try {
                            long time = System.currentTimeMillis();
                            param.converter = param.converters.get(0);
                            ConvertResult ret = operations.convert(param);
                            if (ret.message != null) {
                                element.getIndexFiles().setConverttime("" + (System.currentTimeMillis() - time));
                                element.getIndexFiles().setConvertsw(name);
                            }
                            element.setConvertResult(ret);
                            List<Converter> converters = element.getConvertParam().converters;
                            converters = converters.subList(1, converters.size());
                            element.getConvertParam().converters = converters;
                            String queueName;
                            if (ret.message != null || converters.isEmpty()) {
                                queueName = element.getQueue();
                            } else {
                                queueName = converters.get(0).getName();
                                String appId = System.getenv(Constants.APPID) != null ? System.getenv(Constants.APPID) : "";
                                queueName = queueName + appId;
                            }
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
