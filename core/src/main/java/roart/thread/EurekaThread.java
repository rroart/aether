package roart.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.model.ConfigParam;
import roart.common.service.ServiceResult;
import roart.eureka.util.EurekaUtil;
import roart.service.ControlService;

public class EurekaThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private NodeConfig nodeConf;

    private ControlService controlService;

    private ConfigParam configParam;
    
    public EurekaThread(NodeConfig nodeConf, ControlService controlService, ConfigParam configParam) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
        this.configParam = configParam;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(60 * 1000);
                ConfigParam param = new ConfigParam();
                param.setConf(nodeConf);
                param.setConfigname(controlService.getConfigName());
                param.setConfigid(controlService.getConfigId());
                ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, configParam, EurekaConstants.AETHERSERVICEMANAGER, EurekaConstants.SETCONFIG, nodeConf);
                log.info("got");
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
    }
}
