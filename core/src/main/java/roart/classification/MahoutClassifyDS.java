package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.service.ControlService;

public class MahoutClassifyDS extends ClassifyDS {

    public MahoutClassifyDS(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    public String getAppName() {
    	return EurekaConstants.MAHOUTMR;
    }

    @Override
    public String getQueueName() {
        return QueueConstants.MAHOUTMR;
    }
}

