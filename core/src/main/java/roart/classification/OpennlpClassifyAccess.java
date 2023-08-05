package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.service.ControlService;

public class OpennlpClassifyAccess extends ClassifyAccess {

    public OpennlpClassifyAccess(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    public String getAppName() {
    	return EurekaConstants.OPENNLP;
    }
    
    @Override
    public String getQueueName() {
        return QueueConstants.OPENNLP;
    }
}

