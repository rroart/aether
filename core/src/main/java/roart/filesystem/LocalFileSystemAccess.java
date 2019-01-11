package roart.filesystem;

import roart.common.constants.EurekaConstants;

public class LocalFileSystemAccess extends FileSystemAccess {

    @Override
    public String getAppName() {
        return EurekaConstants.LOCAL;
    }

}
