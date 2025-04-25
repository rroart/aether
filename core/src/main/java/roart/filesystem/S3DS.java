package roart.filesystem;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.service.ControlService;

public class S3DS extends RemoteFileSystemDS {

    public S3DS(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    @Override
    public String getAppName() {
        return EurekaConstants.S3;
    }
    
    @Override
    public String getQueueName() {
        return QueueConstants.S3;
    }

}
