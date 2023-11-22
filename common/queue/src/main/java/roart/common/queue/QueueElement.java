package roart.common.queue;

import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.service.ServiceParam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.database.DatabaseFileLocationParam;
import roart.common.database.DatabaseIndexFilesParam;
import roart.common.database.DatabaseIndexFilesResult;
import roart.common.database.DatabaseMd5Param;
import roart.common.database.DatabaseMd5Result;

public class QueueElement {
    private String id;
    
    private String myid;
    
    private String opid;
    
    private String queue;

    private ServiceParam clientQueueElement;
    
    private String md5;
    
    private String newMd5;
    
    private FileObject fileObject;
    
    private IndexFiles indexFiles;
    
    private IndexFiles newIndexFiles;
    
    private Map<String, String> metadata;
    
    private InmemoryMessage message;

    private long timestamp;
    
    private FileSystemFileObjectParam fileSystemFileObjectParam;
    
    private FileSystemMyFileResult fileSystemMyFileResult;
    
    private FileSystemMessageResult fileSystemMessageResult;
    
    private FileSystemStringResult fileSystemStringResult;
    
    private DatabaseMd5Param databaseMd5Param;
    
    private DatabaseFileLocationParam databaseFileLocationParam;
    
    private DatabaseMd5Result databaseMd5Result;
    
    private DatabaseIndexFilesResult databaseIndexFilesResult;

    private MachineLearningClassifyParam machineLearningClassifyParam;
    
    private MachineLearningClassifyResult machineLearningClassifyResult;
    
    private ConvertParam convertParam;
    
    private ConvertResult convertResult;
    
    private SearchEngineIndexParam searchEngineIndexParam;
    
    private SearchEngineIndexResult searchEngineIndexResult;
    
    // for Jackson
    public QueueElement() {
        super();
    }
    
    public QueueElement(String myid, FileObject fileObject, ServiceParam clientQueueElement, String queue) {
        super();
        this.id = UUID.randomUUID().toString();
        this.myid = myid;
        this.fileObject = fileObject;
        this.clientQueueElement = clientQueueElement;
        this.queue = queue;
        this.metadata = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNewMd5() {
        return newMd5;
    }

    public void setNewMd5(String newMd5) {
        this.newMd5 = newMd5;
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

    public IndexFiles getNewIndexFiles() {
        return newIndexFiles;
    }

    public void setNewIndexFiles(IndexFiles newIndexFiles) {
        this.newIndexFiles = newIndexFiles;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public FileSystemStringResult getFileSystemStringResult() {
        return fileSystemStringResult;
    }

    public void setFileSystemStringResult(FileSystemStringResult fileSystemStringResult) {
        this.fileSystemStringResult = fileSystemStringResult;
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

    public MachineLearningClassifyParam getMachineLearningClassifyParam() {
        return machineLearningClassifyParam;
    }

    public void setMachineLearningClassifyParam(MachineLearningClassifyParam machineLearningClassifyParam) {
        this.machineLearningClassifyParam = machineLearningClassifyParam;
    }

    public MachineLearningClassifyResult getMachineLearningClassifyResult() {
        return machineLearningClassifyResult;
    }

    public void setMachineLearningClassifyResult(MachineLearningClassifyResult machineLearningClassifyResult) {
        this.machineLearningClassifyResult = machineLearningClassifyResult;
    }

    public ConvertParam getConvertParam() {
        return convertParam;
    }

    public void setConvertParam(ConvertParam convertParam) {
        this.convertParam = convertParam;
    }

    public ConvertResult getConvertResult() {
        return convertResult;
    }

    public void setConvertResult(ConvertResult convertResult) {
        this.convertResult = convertResult;
    }

    public SearchEngineIndexParam getSearchEngineIndexParam() {
        return searchEngineIndexParam;
    }

    public void setSearchEngineIndexParam(SearchEngineIndexParam searchEngineIndexParam) {
        this.searchEngineIndexParam = searchEngineIndexParam;
    }

    public FileSystemMessageResult getFileSystemMessageResult() {
        return fileSystemMessageResult;
    }

    public void setFileSystemMessageResult(FileSystemMessageResult fileSystemMessageResult) {
        this.fileSystemMessageResult = fileSystemMessageResult;
    }

    public SearchEngineIndexResult getSearchEngineIndexResult() {
        return searchEngineIndexResult;
    }

    public void setSearchEngineIndexResult(SearchEngineIndexResult searchEngineIndexResult) {
        this.searchEngineIndexResult = searchEngineIndexResult;
    }

}
