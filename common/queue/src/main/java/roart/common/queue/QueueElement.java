package roart.common.queue;

import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;

import java.util.HashMap;
import java.util.Map;

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
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.database.DatabaseFileLocationParam;
import roart.common.database.DatabaseIndexFilesParam;
import roart.common.database.DatabaseIndexFilesResult;
import roart.common.database.DatabaseMd5Param;
import roart.common.database.DatabaseMd5Result;

public class QueueElement {
    private String myid;
    
    private String opid;
    
    private String queue;

    private ServiceParam clientQueueElement;
    
    private String md5;
    
    private FileObject fileObject;
    
    private IndexFiles indexFiles;
    
    private Map<String, String> metadata;
    
    private InmemoryMessage message;

    private FileSystemFileObjectParam fileSystemFileObjectParam;
    
    private FileSystemMyFileResult fileSystemMyFileResult;
    
    private DatabaseMd5Param databaseMd5Param;
    
    private DatabaseFileLocationParam databaseFileLocationParam;
    
    private DatabaseMd5Result databaseMd5Result;
    
    private DatabaseIndexFilesResult databaseIndexFilesResult;
    
    // for Jackson
    public QueueElement() {
        super();
    }
    
    public QueueElement(String myid, FileObject fileObject, ServiceParam clientQueueElement, String queue) {
        super();
        this.myid = myid;
        this.fileObject = fileObject;
        this.clientQueueElement = clientQueueElement;
        this.queue = queue;
        this.metadata = new HashMap<>();
    }

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public ServiceParam getClientQueueElement() {
        return clientQueueElement;
    }

    public void setClientQueueElement(ServiceParam clientQueueElement) {
        this.clientQueueElement = clientQueueElement;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    public void setFileObject(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    public IndexFiles getIndexFiles() {
        return indexFiles;
    }

    public void setIndexFiles(IndexFiles indexFiles) {
        this.indexFiles = indexFiles;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public InmemoryMessage getMessage() {
        return message;
    }

    public void setMessage(InmemoryMessage message) {
        this.message = message;
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

    public DatabaseMd5Param getDatabaseMd5Param() {
        return databaseMd5Param;
    }

    public void setDatabaseMd5Param(DatabaseMd5Param databaseMd5Param) {
        this.databaseMd5Param = databaseMd5Param;
    }

    public DatabaseFileLocationParam getDatabaseFileLocationParam() {
        return databaseFileLocationParam;
    }

    public void setDatabaseFileLocationParam(DatabaseFileLocationParam databaseFileLocationParam) {
        this.databaseFileLocationParam = databaseFileLocationParam;
    }

    public DatabaseMd5Result getDatabaseMd5Result() {
        return databaseMd5Result;
    }

    public void setDatabaseMd5Result(DatabaseMd5Result databaseMd5Result) {
        this.databaseMd5Result = databaseMd5Result;
    }

    public DatabaseIndexFilesResult getDatabaseIndexFilesResult() {
        return databaseIndexFilesResult;
    }

    public void setDatabaseIndexFilesResult(DatabaseIndexFilesResult databaseIndexFilesResult) {
        this.databaseIndexFilesResult = databaseIndexFilesResult;
    }
    
}
