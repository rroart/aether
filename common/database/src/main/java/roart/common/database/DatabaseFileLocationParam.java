package roart.common.database;

import java.util.Set;

import roart.common.model.FileLocation;

public class DatabaseFileLocationParam extends DatabaseParam {
    private FileLocation fileLocation;

    private Set<FileLocation> fileLocations;
    
    public DatabaseFileLocationParam() {
        super();
    }

    public FileLocation getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(FileLocation fileLocation) {
        this.fileLocation = fileLocation;
    }

    public Set<FileLocation> getFileLocations() {
        return fileLocations;
    }

    public void setFileLocations(Set<FileLocation> fileLocations) {
        this.fileLocations = fileLocations;
    }
    
}
