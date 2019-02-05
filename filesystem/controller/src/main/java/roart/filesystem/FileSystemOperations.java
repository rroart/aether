package roart.filesystem;

import roart.common.filesystem.FileSystemBooleanResult;
import roart.common.filesystem.FileSystemByteResult;
import roart.common.filesystem.FileSystemConstructorResult;
import roart.common.filesystem.FileSystemFileObjectParam;
import roart.common.filesystem.FileSystemFileObjectResult;
import roart.common.filesystem.FileSystemMyFileResult;
import roart.common.filesystem.FileSystemPathParam;
import roart.common.filesystem.FileSystemPathResult;

public abstract class FileSystemOperations {
    
    public abstract FileSystemConstructorResult destroy() throws Exception;
	
    public abstract FileSystemFileObjectResult listFiles(FileSystemFileObjectParam param);
    
    public abstract FileSystemMyFileResult listFilesFull(FileSystemFileObjectParam param) throws Exception;
    
    public abstract FileSystemBooleanResult exists(FileSystemFileObjectParam param);
    
    public abstract FileSystemPathResult getAbsolutePath(FileSystemFileObjectParam param);

    public abstract FileSystemBooleanResult isDirectory(FileSystemFileObjectParam param);

    public abstract FileSystemByteResult getInputStream(FileSystemFileObjectParam param) throws Exception;
    
    public abstract FileSystemMyFileResult getWithInputStream(FileSystemPathParam param) throws Exception;
    
    public abstract FileSystemFileObjectResult getParent(FileSystemFileObjectParam param);
    
    public abstract FileSystemFileObjectResult get(FileSystemPathParam param);

}
