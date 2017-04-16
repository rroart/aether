package roart.filesystem;

import roart.util.EurekaConstants;

public class LocalFileSystemAccess extends FileSystemAccess {

    @Override
    public String getAppName() {
        return EurekaConstants.LOCALFILESYSTEM;
    }

}
