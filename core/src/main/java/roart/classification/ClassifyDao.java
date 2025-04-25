package roart.classification;

import roart.common.collections.MyQueue;
import roart.common.config.NodeConfig;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.queue.QueueElement;
import roart.service.ControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifyDao {
    private static Logger log = LoggerFactory.getLogger(ClassifyDao.class);

    public ClassifyDS classify = null;

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public ClassifyDao(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.classify = ClassifyDSFactory.get(nodeConf, controlService);
        this.controlService = controlService;
    }

    public String classify(InmemoryMessage message, String language) {
        if (classify == null) {
            return null;
        }
        return classify.classify(message, language);
    }


    public void classifyQueue(QueueElement element, InmemoryMessage message, String language) {
        if (classify == null) {
            return;
        }
        classify.classifyQueue(element, message, language);
    }
    
    public MyQueue<QueueElement> getQueue() {
        return classify.getQueue();
    }
}
