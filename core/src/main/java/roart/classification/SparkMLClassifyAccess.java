package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.service.ControlService;

public class SparkMLClassifyAccess extends ClassifyAccess {

    public SparkMLClassifyAccess(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    public String getAppName() {
    	return EurekaConstants.SPARKML;
    }

    @Override
    public String getQueueName() {
        return QueueConstants.SPARKML;
    }
}

