package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class MahoutSparkClassifyAccess extends ClassifyAccess {

    public MahoutSparkClassifyAccess(NodeConfig nodeConf) {
        super(nodeConf);
    }

    public String getAppName() {
    	return EurekaConstants.MAHOUTSPARK;
    }
 }

