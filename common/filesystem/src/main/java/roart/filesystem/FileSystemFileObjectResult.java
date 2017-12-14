package roart.filesystem;

import roart.model.FileObject;

public class FileSystemFileObjectResult extends FileSystemResult {
    FileObject[] fileObject;

    public FileObject[] getFileObject() {
        return fileObject;
    }

    public void setFileObject(FileObject[] fileObject) {
        this.fileObject = fileObject;
    }
}
