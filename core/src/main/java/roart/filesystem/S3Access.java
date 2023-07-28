package roart.filesystem;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class S3Access extends RemoteFileSystemAccess{

    public S3Access(NodeConfig nodeConf) {
        super(nodeConf);
    }

    @Override
    public String getAppName() {
        return EurekaConstants.S3;
    }
}
