package roart.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.service.ServiceParam;
import roart.common.service.ServiceResult;
import roart.eureka.util.EurekaUtil;

public class EurekaThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    Object parameter;

    public EurekaThread(Object parameter) {
        this.parameter = parameter;
    }

    public void run() {
        boolean noException = false;
        while (noException == false) {
            try {
                ServiceParam param = new ServiceParam();
                param.config = (NodeConfig) parameter;
                ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, EurekaConstants.AETHERSERVICEMANAGER, EurekaConstants.SETCONFIG);
                noException = true;
                log.info("got");
            } catch (Exception e) {
                log.error("Ex, sleep 15", e);
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    log.error("Exception", ex);
                }
                //EurekaUtil.initEurekaClient();
            }
        }
        log.info("sent");
    }
}
