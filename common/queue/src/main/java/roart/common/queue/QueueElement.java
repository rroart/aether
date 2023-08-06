package roart.common.queue;

import roart.common.model.FileObject;
import roart.common.service.ServiceParam;
import roart.common.filesystem.FileSystemBooleanResult;
import roart.common.filesystem.FileSystemByteResult;
import roart.common.filesystem.FileSystemConstructorParam;
import roart.common.filesystem.FileSystemConstructorResult;
import roart.common.filesystem.FileSystemFileObjectParam;
import roart.common.filesystem.FileSystemFileObjectResult;
import roart.common.filesystem.FileSystemMessageResult;
import roart.common.filesystem.FileSystemMyFileResult;
import roart.common.filesystem.FileSystemParam;
import roart.common.filesystem.FileSystemPathParam;
import roart.common.filesystem.FileSystemPathResult;
import roart.common.filesystem.FileSystemStringResult;

public class QueueElement {
    private String myid;
    private FileObject fileObject;
    private ServiceParam clientQueueElement;
    private String opid;
    private FileSystemFileObjectParam fileSystemFileObjectParam;
    private FileSystemMyFileResult fileSystemMyFileResult;
    public String getMyid() {
        return myid;
    }
    public void setMyid(String myid) {
        this.myid = myid;
    }
    public FileObject getFileObject() {
        return fileObject;
    }
    public void setFileobject(FileObject fileObject) {
        this.fileObject = fileObject;
    }
    public ServiceParam getClientQueueElement() {
        return clientQueueElement;
    }
    public void setClientQueueElement(ServiceParam clientQueueElement) {
        this.clientQueueElement = clientQueueElement;
    }
    public String getOpid() {
        return opid;
    }
    public void setOpid(String opid) {
        this.opid = opid;
    }
    public FileSystemFileObjectParam getFileSystemFileObjectParam() {
        return fileSystemFileObjectParam;
    }
    public void setFileSystemFileObjectParam(FileSystemFileObjectParam fileSystemFileObjectParam) {
        this.fileSystemFileObjectParam = fileSystemFileObjectParam;
    }
    public FileSystemMyFileResult getFileSystemMyFileResult() {
        return fileSystemMyFileResult;
    }
    public void setFileSystemMyFileResult(FileSystemMyFileResult fileSystemMyFileResult) {
        this.fileSystemMyFileResult = fileSystemMyFileResult;
    }
    
}
