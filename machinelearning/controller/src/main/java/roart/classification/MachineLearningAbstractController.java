package roart.classification;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.common.machinelearning.MachineLearningConstructorParam;
import roart.common.machinelearning.MachineLearningConstructorResult;
import roart.common.machinelearning.MachineLearningParam;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.util.InmemoryUtil;
import roart.common.util.IOUtil;
import roart.common.util.JsonUtil;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;

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

    private MachineLearningAbstractClassifier getClassifier(MachineLearningParam param) {
        MachineLearningAbstractClassifier classifier = classifierMap.get(param.configid);
        if (classifier == null) {
            NodeConfig nodeConf = null;
            if (param.conf == null) {
                Inmemory inmemory = InmemoryFactory.get(param.iserver, param.configname, param.iconnection);
                try (InputStream contentStream = inmemory.getInputStream(param.iconf)) {
                    if (InmemoryUtil.validate(param.iconf.getMd5(), contentStream)) {
                        String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArray1G(inmemory.getInputStream(param.iconf)));
                        nodeConf = JsonUtil.convertnostrip(content, NodeConfig.class);
                    }
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            } else {
                nodeConf = param.conf;
            }
            classifier = createClassifier(param.configname, param.configid, nodeConf);
            classifierMap.put(param.configid, classifier);
        }
        return classifier;
    }

    protected abstract MachineLearningAbstractClassifier createClassifier(String configname, String configid, NodeConfig nodeConf);

    @RequestMapping(value = "/" + EurekaConstants.CONSTRUCTOR,
            method = RequestMethod.POST)
    public MachineLearningConstructorResult processConstructor(@RequestBody MachineLearningConstructorParam param)
            throws Exception {
        String error = null;
        try {
            MachineLearningAbstractClassifier classifier = getClassifier(param);
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
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
        MachineLearningAbstractClassifier classifier = classifierMap.remove(param.configid);
        String error = null;
        if (classifier != null) {
            try {
                classifier.destroy(param.configname);
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
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
        MachineLearningAbstractClassifier classifier = getClassifier(param);
        MachineLearningClassifyResult ret = classifier.classify(param);
        return ret;
    }

    public abstract String getQueueName();
    
}
