package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class MahoutClassifyAccess extends ClassifyAccess {

    public MahoutClassifyAccess(NodeConfig nodeConf) {
        super(nodeConf);
    }

    public String getAppName() {
    	return EurekaConstants.MAHOUTMR;
    }
 }

