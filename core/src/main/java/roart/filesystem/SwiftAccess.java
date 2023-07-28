package roart.filesystem;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class SwiftAccess extends RemoteFileSystemAccess {

    public SwiftAccess(NodeConfig nodeConf) {
        super(nodeConf);
    }

    @Override
    public String getAppName() {
        return EurekaConstants.SWIFT;
    }

}
