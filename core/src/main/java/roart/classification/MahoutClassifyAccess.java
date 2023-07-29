package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.service.ControlService;

public class MahoutClassifyAccess extends ClassifyAccess {

    public MahoutClassifyAccess(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    public String getAppName() {
    	return EurekaConstants.MAHOUTMR;
    }
 }

