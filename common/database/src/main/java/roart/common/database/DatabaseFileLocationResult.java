package roart.common.database;

import roart.common.model.FileLocation;

public class DatabaseFileLocationResult extends DatabaseResult {
    public FileLocation[] fileLocation;

    public FileLocation[] getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(FileLocation[] fileLocation) {
        this.fileLocation = fileLocation;
    }
}
