package roart.common.filesystem;

import java.util.Set;

import roart.common.model.FileObject;

public class FileSystemFileObjectParam extends FileSystemParam {
    public FileObject fo;
    
    public Set<FileObject> fos;

    public FileSystemFileObjectParam() {
    }

    public FileSystemFileObjectParam(FileObject fo) {
        this.fo = fo;
    }

}
