package roart.filesystem;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.service.ControlService;

public class S3Access extends RemoteFileSystemAccess{

    public S3Access(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    @Override
    public String getAppName() {
        return EurekaConstants.S3;
    }
}
