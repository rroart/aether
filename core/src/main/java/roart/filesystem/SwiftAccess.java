package roart.filesystem;

import roart.util.EurekaConstants;

public class SwiftAccess extends RemoteFileSystemAccess {

    @Override
    public String getAppName() {
        return EurekaConstants.SWIFT;
    }

}
