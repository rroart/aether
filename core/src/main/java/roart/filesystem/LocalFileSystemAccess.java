package roart.filesystem;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class LocalFileSystemAccess extends FileSystemAccess {

    public LocalFileSystemAccess(NodeConfig nodeConf) {
        super(nodeConf);
    }

    @Override
    public String getAppName() {
        return EurekaConstants.LOCAL;
    }

}
