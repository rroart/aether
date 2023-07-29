package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.service.ControlService;

public class MahoutSparkClassifyAccess extends ClassifyAccess {

    public MahoutSparkClassifyAccess(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    public String getAppName() {
    	return EurekaConstants.MAHOUTSPARK;
    }
 }

