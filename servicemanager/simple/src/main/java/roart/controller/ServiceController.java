package roart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.service.ServiceParam;
import roart.common.service.ServiceResult;
import roart.config.MyXMLConfig;

@RestController
public class ServiceController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/" + EurekaConstants.SETCONFIG,
            method = RequestMethod.POST)
    public ServiceResult setConfig(@RequestBody ServiceParam param) {
        ServiceResult result = new ServiceResult();
        try {
            MyXMLConfig.instance(param.config);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

}
