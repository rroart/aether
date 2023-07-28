package roart.filesystem;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class HDFSAccess extends RemoteFileSystemAccess {

    public HDFSAccess(NodeConfig nodeConf) {
        super(nodeConf);
    }

    @Override
    public String getAppName() {
        return EurekaConstants.HDFS;
    }

}
