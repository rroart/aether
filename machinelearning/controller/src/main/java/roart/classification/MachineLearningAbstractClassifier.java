package roart.classification;

import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.common.machinelearning.MachineLearningConstructorResult;

public abstract class MachineLearningAbstractClassifier {

    public abstract MachineLearningConstructorResult destroy(String nodename);

    public abstract MachineLearningClassifyResult classify(MachineLearningClassifyParam classify);
    
}
