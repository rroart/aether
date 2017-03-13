package roart.classification;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import roart.common.machinelearning.MachineLearningConstructorParam;
import roart.common.machinelearning.MachineLearningConstructorResult;
import roart.config.NodeConfig;
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.util.EurekaConstants;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
//@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
public abstract class MachineLearningAbstractController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static Map<String, MachineLearningAbstractClassifier> classifierMap = new HashMap();
    
    private MachineLearningAbstractClassifier getClassifier(String nodename, NodeConfig nodeConf) {
		MachineLearningAbstractClassifier classifier = classifierMap.get(nodename);
		if (classifier == null) {
	    	classifier = createClassifier(nodename, nodeConf);
			classifierMap.put(nodename, classifier);
		}
		return classifier;
    }

    protected abstract MachineLearningAbstractClassifier createClassifier(String nodename, NodeConfig nodeConf);
    
    @RequestMapping(value = "/" + EurekaConstants.CONSTRUCTOR,
		    method = RequestMethod.POST)
		    public MachineLearningConstructorResult processConstructor(@RequestBody MachineLearningConstructorParam param)
	throws Exception {
    	String error = null;
    	try {
      	MachineLearningAbstractClassifier classifier = getClassifier(param.nodename, param.conf);
		} catch (Exception e) {
		    log.error(roart.util.Constants.EXCEPTION, e);
		    error = e.getMessage();
		}
		MachineLearningConstructorResult result = new MachineLearningConstructorResult();
		result.error = error;
		return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DESTRUCTOR,
		    method = RequestMethod.POST)
		    public MachineLearningConstructorResult processDestructor(@RequestBody MachineLearningConstructorParam param)
	throws Exception {
		MachineLearningAbstractClassifier classifier = classifierMap.remove(param.nodename);
		String error = null;
		if (classifier != null) {
			try {
			classifier.destroy(param.nodename);
		} catch (Exception e) {
		    log.error(roart.util.Constants.EXCEPTION, e);
		    error = e.getMessage();
		}
		} else {
			error = "did not exist";
		}
		MachineLearningConstructorResult result = new MachineLearningConstructorResult();
		result.error = error;
		return result;
   }

    @RequestMapping(value = "/" + EurekaConstants.CLASSIFY,
		    method = RequestMethod.POST)
		    public MachineLearningClassifyResult processClassify(@RequestBody MachineLearningClassifyParam param)
	throws Exception {
       	MachineLearningAbstractClassifier classifier = getClassifier(param.nodename, param.conf);
    	MachineLearningClassifyResult ret = classifier.classify(param);
    	return ret;
    }

}
