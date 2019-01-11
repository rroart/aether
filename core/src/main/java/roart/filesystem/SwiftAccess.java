package roart.filesystem;

import roart.common.constants.EurekaConstants;

public class SwiftAccess extends RemoteFileSystemAccess {

    @Override
    public String getAppName() {
        return EurekaConstants.SWIFT;
    }

}
