package roart.filesystem;

import roart.util.EurekaConstants;

public class HDFSAccess extends RemoteFileSystemAccess {

    @Override
    public String getAppName() {
        return EurekaConstants.HDFS;
    }

}
