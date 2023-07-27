package roart.classification;

import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.common.machinelearning.MachineLearningConstructorParam;
import roart.common.machinelearning.MachineLearningConstructorResult;
import roart.common.machinelearning.MachineLearningParam;
import roart.common.machinelearning.MachineLearningResult;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.eureka.util.EurekaUtil;
import roart.service.ControlService;

import java.util.List;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClassifyAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public abstract String getAppName();

    public String constructor() {
        MachineLearningConstructorParam param = new MachineLearningConstructorParam();
        configureParam(param);
        MachineLearningConstructorResult result = EurekaUtil.sendMe(MachineLearningConstructorResult.class, param, getAppName(), EurekaConstants.CONSTRUCTOR);   	
        return result.error;
    }
    
    public String destructor() {
        MachineLearningConstructorParam param = new MachineLearningConstructorParam();
        configureParam(param);
        MachineLearningConstructorResult result = EurekaUtil.sendMe(MachineLearningConstructorResult.class, param, getAppName(), EurekaConstants.DESTRUCTOR);   	
        return result.error;
    }
    
    public String classify( InmemoryMessage message, String language) {
    	MachineLearningClassifyParam param = new MachineLearningClassifyParam();
        configureParam(param);
    	param.message = message;
    	param.language = language;
        MachineLearningClassifyResult result = EurekaUtil.sendMe(MachineLearningClassifyResult.class, param, getAppName(), EurekaConstants.CLASSIFY);

        if (result == null) {
        	return null;
        }
        
        return result.result;
    }

    private void configureParam(MachineLearningParam param) {
        param.configname = ControlService.getConfigName();
        param.configid = ControlService.getConfigId();
        param.iconf = ControlService.iconf;
        param.iserver = MyConfig.conf.getInmemoryServer();
        if (Constants.REDIS.equals(MyConfig.conf.getInmemoryServer())) {
            param.iconnection = MyConfig.conf.getInmemoryRedis();
        }
   }

}

