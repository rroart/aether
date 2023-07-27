package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.common.machinelearning.MachineLearningConstructorResult;

public abstract class MachineLearningAbstractClassifier {

    protected final String configname;
    protected final String configid;
    protected final NodeConfig nodeConf;
    
    public MachineLearningAbstractClassifier(String configname, String configid, NodeConfig nodeConf) {
        this.configname = configname;
        this.configid = configid;
        this.nodeConf = nodeConf;
    }

    public abstract MachineLearningConstructorResult destroy(String configname);

    public abstract MachineLearningClassifyResult classify(MachineLearningClassifyParam classify);
    
}
