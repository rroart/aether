package roart.classification;

import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.common.machinelearning.MachineLearningConstructorParam;
import roart.common.machinelearning.MachineLearningConstructorResult;
import roart.config.NodeConfig;
import roart.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.FileInputStream;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpennlpClassify extends MachineLearningAbstractClassifier {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private OpenNLPConfig conf;
    
    public OpennlpClassify(String nodename, NodeConfig nodeConf) {
	try {
	    conf = new OpenNLPConfig();
		conf.categorizerMap = new HashMap<String, DocumentCategorizerME>();
		String[] languages = nodeConf.getLanguages();
	    String modelFilePath = nodeConf.getOpenNLPModelPath(); 
	    for (String lang : languages) {
	    	String path = new String(modelFilePath);
	    	path = path.replaceFirst("LANG", lang);
	    	InputStream is = new FileInputStream(path);
	    	DoccatModel model = new DoccatModel(is);
	    	DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
	    	conf.categorizerMap.put(lang, myCategorizer);
	    }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

	public MachineLearningConstructorResult destroy(String nodename) {
		return null;
	}
	
   public MachineLearningClassifyResult classify(MachineLearningClassifyParam classify) {
    		String type = classify.str;
    		String language = classify.language;
    	DocumentCategorizerME myCategorizer = conf.categorizerMap.get(language);
	double[] outcomes = myCategorizer.categorize(type);
	String category = myCategorizer.getBestCategory(outcomes);
	log.info("opennlp cat " + category);
	MachineLearningClassifyResult result = new MachineLearningClassifyResult();
	result.result = category;
	return result;
    }


}

