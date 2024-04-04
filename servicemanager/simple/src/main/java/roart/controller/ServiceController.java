package roart.controller;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.model.ConfigParam;
import roart.common.service.ServiceResult;
import roart.config.MyXMLConfig;
import roart.common.config.NodeConfig;

@RestController
public class ServiceController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static Map<String, NodeConfig> confMap = new HashMap<>();

    @RequestMapping(value = "/" + EurekaConstants.SETCONFIG,
            method = RequestMethod.POST)
    public ServiceResult setConfig(@RequestBody ConfigParam param) {
        ServiceResult result = new ServiceResult();
        try {
            String key = param.getConfigid();
            NodeConfig conf = confMap.get(key);
            if (conf == null) {
                conf = param.getConf();
                confMap.put(key, conf);
                MyXMLConfig.instance(conf);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

}
