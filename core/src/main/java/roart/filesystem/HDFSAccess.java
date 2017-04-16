package roart.filesystem;

import roart.util.EurekaConstants;

public class HDFSAccess extends FileSystemAccess {

    @Override
    public String getAppName() {
        return EurekaConstants.HDFS;
    }

}
