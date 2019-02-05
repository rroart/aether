package roart.common.filesystem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonIgnore;

import roart.common.model.FileObject;

public class MyFile {
    public boolean exists;

    public boolean isDirectory;
    
    public String absolutePath;
    
    public FileObject[] fileObject;

    public byte[] bytes;

    @JsonIgnore
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }
}
