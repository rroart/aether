package roart.filesystem;

import roart.common.constants.EurekaConstants;

public class HDFSAccess extends RemoteFileSystemAccess {

    @Override
    public String getAppName() {
        return EurekaConstants.HDFS;
    }

}
