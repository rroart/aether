package roart.classification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.service.ControlService;

public class ClassifyDSFactory {

    private static Logger log = LoggerFactory.getLogger(ClassifyDSFactory.class);
    
    public static ClassifyDS get(NodeConfig nodeConf, ControlService controlService) {
        ClassifyDS classify = null;
        String type = configClassify(nodeConf);
        if (type.equals(ConfigConstants.MACHINELEARNINGMAHOUT)) {
            classify = new MahoutClassifyDS(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.MACHINELEARNINGMAHOUTSPARK)) {
            classify = new MahoutSparkClassifyDS(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.MACHINELEARNINGSPARKML)) {
            classify = new SparkMLClassifyDS(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.MACHINELEARNINGOPENNLP)) {
            classify = new OpennlpClassifyDS(nodeConf, controlService);
        }
        return classify;
    }

    public static String configClassify(NodeConfig configInstance) {
        if (!configInstance.wantClassify()) {
            return null;
        }
        try {
            String classify = null;
            if (configInstance.wantSparkML()) {
                classify = ConfigConstants.MACHINELEARNINGSPARKML;
            } else if (configInstance.wantMahoutSpark()) {
                classify = ConfigConstants.MACHINELEARNINGMAHOUTSPARK;
            } else if (configInstance.wantMahout()) {
                classify = ConfigConstants.MACHINELEARNINGMAHOUT;
            } else if (configInstance.wantOpenNLP()) {
                classify = ConfigConstants.MACHINELEARNINGOPENNLP;
            }
            if (classify != null) {
                //roart.classification.ClassifyDao.instance(classify);
            }
            return classify;
        } catch (Exception e) {
            // TODO propagate
            log.error(Constants.EXCEPTION, e); 
            return null;
        }
    }

}
