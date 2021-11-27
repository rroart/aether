package roart.filesystem;

import roart.common.constants.EurekaConstants;

public class S3Access extends RemoteFileSystemAccess{

    @Override
    public String getAppName() {
        return EurekaConstants.S3;
    }
}
