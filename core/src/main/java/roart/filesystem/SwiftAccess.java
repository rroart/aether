package roart.filesystem;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.service.ControlService;

public class SwiftAccess extends RemoteFileSystemAccess {

    public SwiftAccess(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    @Override
    public String getAppName() {
        return EurekaConstants.SWIFT;
    }

    @Override
    public String getQueueName() {
        return QueueConstants.SWIFT;
    }

}
