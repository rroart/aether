package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.service.ControlService;

public class MahoutSparkClassifyDS extends ClassifyDS {

    public MahoutSparkClassifyDS(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    public String getAppName() {
    	return EurekaConstants.MAHOUTSPARK;
    }

    @Override
    public String getQueueName() {
        return QueueConstants.MAHOUTSPARK;
    }
 }

