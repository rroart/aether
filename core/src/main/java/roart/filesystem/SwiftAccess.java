package roart.filesystem;

import roart.util.EurekaConstants;

public class SwiftAccess extends FileSystemAccess {

    @Override
    public String getAppName() {
        return EurekaConstants.SWIFT;
    }

}
