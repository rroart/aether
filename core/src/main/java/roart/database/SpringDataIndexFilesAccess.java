package roart.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.service.ControlService;

public class SpringDataIndexFilesAccess extends IndexFilesAccess {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public SpringDataIndexFilesAccess(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    public String getAppName() {
        return EurekaConstants.SPRINGDATA;
    }

    @Override
    public String getQueueName() {
        return QueueConstants.SPRING;
    }

    @Override
    public boolean queueWithAppId() {
        return true;
    }
}
