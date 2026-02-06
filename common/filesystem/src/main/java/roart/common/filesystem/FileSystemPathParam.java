package roart.common.filesystem;

import java.util.Set;

import roart.common.model.FileObject;

public class FileSystemPathParam extends FileSystemParam {
    public FileObject path;
    
    public Set<FileObject> paths;

    public FileSystemPathParam() {
    }

    public FileSystemPathParam(FileObject fo) {
        this.path = fo;
    }

}
