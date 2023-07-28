package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class OpennlpClassifyAccess extends ClassifyAccess {

    public OpennlpClassifyAccess(NodeConfig nodeConf) {
        super(nodeConf);
    }

    public String getAppName() {
    	return EurekaConstants.OPENNLP;
    }
}

