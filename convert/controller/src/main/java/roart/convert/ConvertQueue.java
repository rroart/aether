package roart.convert;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Component;

import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.Converter;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.OperationConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.queue.QueueElement;

public class ConvertQueue {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ConvertQueue(String name, ConvertAbstractController controller, CuratorFramework curatorClient, NodeConfig nodeConf) {
        final HazelcastInstance hz;
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
            hz = HazelcastClient.newHazelcastClient();
        } else {
            hz = null;
        }
        HazelcastInstance ahz = hz;
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
                    log.info("Opid {} {}", element.getOpid(), element.getQueue());
                    if (element.getOpid().equals(OperationConstants.CONVERT)) {
                        ConvertParam param = element.getConvertParam();
                        element.setFileSystemFileObjectParam(null);
                        ConvertAbstract operations = controller.getConvert(param);
                        try {
                            long time = System.currentTimeMillis();
                            ConvertResult ret = operations.convert(param);
                            if (ret.message != null) {
                                element.getIndexFiles().setConverttime("" + (System.currentTimeMillis() - time));
                                element.getIndexFiles().setConvertsw(name);
                            }
                            element.setConvertResult(ret);
                            String queueName;
                            if (ret.message != null || element.getConvertParam().converters.isEmpty()) {
                                queueName = element.getQueue();
                            } else {
                                List<Converter> converters = element.getConvertParam().converters;
                                queueName = converters.get(0).getName();
                                element.getConvertParam().converters = converters.subList(1, converters.size());
                            }
                            MyQueue<QueueElement> returnQueue =  new MyQueueFactory().create(queueName, nodeConf, curatorClient, ahz);
                            element.setQueue(name);
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
