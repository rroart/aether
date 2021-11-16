package roart.classification;

import roart.common.config.NodeConfig;
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.common.machinelearning.MachineLearningConstructorResult;

public abstract class MachineLearningAbstractClassifier {

    protected final String nodename;
    protected final NodeConfig nodeConf;
    
    public MachineLearningAbstractClassifier(String nodename, NodeConfig nodeConf) {
        this.nodename = nodename;
        this.nodeConf = nodeConf;
    }

    public abstract MachineLearningConstructorResult destroy(String nodename);

    public abstract MachineLearningClassifyResult classify(MachineLearningClassifyParam classify);
    
}
