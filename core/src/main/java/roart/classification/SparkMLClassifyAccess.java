package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class SparkMLClassifyAccess extends ClassifyAccess {

    public SparkMLClassifyAccess(NodeConfig nodeConf) {
        super(nodeConf);
    }

    public String getAppName() {
    	return EurekaConstants.SPARKML;
    }
}

