package roart.common.database;

import roart.common.model.FileLocation;

public class DatabaseFileLocationParam extends DatabaseParam {
    private FileLocation fileLocation;

    public DatabaseFileLocationParam() {
        super();
    }

    public FileLocation getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(FileLocation fileLocation) {
        this.fileLocation = fileLocation;
    }
    
}
