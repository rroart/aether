package roart.filesystem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.constants.OperationConstants;
import roart.common.filesystem.FileSystemBooleanResult;
import roart.common.filesystem.FileSystemByteResult;
import roart.common.filesystem.FileSystemConstructorParam;
import roart.common.filesystem.FileSystemConstructorResult;
import roart.common.filesystem.FileSystemFileObjectParam;
import roart.common.filesystem.FileSystemFileObjectResult;
import roart.common.filesystem.FileSystemMessageResult;
import roart.common.filesystem.MyFile;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.filesystem.FileSystemMyFileResult;
import roart.common.filesystem.FileSystemParam;
import roart.common.filesystem.FileSystemPathParam;
import roart.common.filesystem.FileSystemPathResult;
import roart.common.filesystem.FileSystemStringResult;
import roart.common.model.FileObject;
import roart.common.queue.QueueElement;
import roart.common.webflux.WebFluxUtil;
import roart.service.ControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemDS {

    private String url;
    
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public String getAppName() { return null; }
 
    protected NodeConfig nodeConf;

    private ControlService controlService;

    private String queueName;

    private MyQueue<QueueElement> queue;
    
    public FileSystemDS(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public String constructor(String url) {
        this.url = url;
        FileSystemConstructorParam param = new FileSystemConstructorParam();
        configureParam(param);
        //FileSystemConstructorResult result = WebFluxUtil.sendMe(FileSystemConstructorResult.class, param, getAppName(), EurekaConstants.CONSTRUCTOR);
        FileSystemConstructorResult result = WebFluxUtil.sendMe(FileSystemConstructorResult.class, url, param, EurekaConstants.CONSTRUCTOR);
        return result.error;
    }

    public String destructor() {
        FileSystemConstructorParam param = new FileSystemConstructorParam();
        configureParam(param);
        FileSystemConstructorResult result = WebFluxUtil.sendMe(FileSystemConstructorResult.class, url, param, EurekaConstants.DESTRUCTOR);
        return result.error;
    }
    public List<FileObject> listFiles(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fo = f;
        FileSystemFileObjectResult result = WebFluxUtil.sendMe(FileSystemFileObjectResult.class, url, param, EurekaConstants.LISTFILES);
        return Arrays.asList(result.getFileObject());

    }

    public List<MyFile> listFilesFull(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fo = f;
        FileSystemMyFileResult result = WebFluxUtil.sendMe(FileSystemMyFileResult.class, url, param, EurekaConstants.LISTFILESFULL);
        return new ArrayList<>(result.map.values());

    }

    public boolean exists(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fo = f;
        FileSystemBooleanResult result = WebFluxUtil.sendMe(FileSystemBooleanResult.class, url, param, EurekaConstants.EXIST);
        return result.bool;

    }

    public String getAbsolutePath(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fo = f;
        FileSystemPathResult result = WebFluxUtil.sendMe(FileSystemPathResult.class, url, param, EurekaConstants.GETABSOLUTEPATH);
        return result.getPath();

    }

    public boolean isDirectory(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fo = f;
        FileSystemBooleanResult result = WebFluxUtil.sendMe(FileSystemBooleanResult.class, url, param, EurekaConstants.ISDIRECTORY);
        return result.bool;

    }

    public InputStream getInputStream(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fo = f;
        FileSystemByteResult result = WebFluxUtil.sendMe(FileSystemByteResult.class, url, param, EurekaConstants.GETINPUTSTREAM);
        return new ByteArrayInputStream(result.bytes);

    }

    public Map<String, MyFile> getWithInputStream(Set<FileObject> filenames) {
        FileSystemPathParam param = new FileSystemPathParam();
        configureParam(param);
        param.paths = filenames;
        FileSystemMyFileResult result = WebFluxUtil.sendMe(FileSystemMyFileResult.class, url, param, EurekaConstants.GETWITHINPUTSTREAM);
        return result.map;

    }

    public Map<String, MyFile> getWithoutInputStream(Set<FileObject> filenames) {
        FileSystemPathParam param = new FileSystemPathParam();
        configureParam(param);
        param.paths = filenames;
        FileSystemMyFileResult result = WebFluxUtil.sendMe(FileSystemMyFileResult.class, url, param, EurekaConstants.GETWITHOUTINPUTSTREAM);
        return result.map;

    }

    public FileObject getParent(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fo = f;
        FileSystemFileObjectResult result = WebFluxUtil.sendMe(FileSystemFileObjectResult.class, url, param, EurekaConstants.GETPARENT);
        return result.getFileObject()[0];

    }

    public FileObject get(FileObject fo) {
        FileSystemPathParam param = new FileSystemPathParam();
        configureParam(param);
        param.path = fo;
        FileSystemFileObjectResult result = WebFluxUtil.sendMe(FileSystemFileObjectResult.class, url, param, EurekaConstants.GET);
        return result.getFileObject()[0];

    }

    @Deprecated
    public String getLocalFilesystemFile(FileObject fo) {
        FileObject file = new FileSystemDao(nodeConf, controlService).get(fo);  
        String fn = new FileSystemDao(nodeConf, controlService).getAbsolutePath(file);
        // TODO
        if (fn.charAt(4) == ':') {
            fn = fn.substring(5);
        }
        return fn;
    }

    public Map<String, InmemoryMessage> readFile(Set<FileObject> f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fos = f;
        FileSystemMessageResult result = WebFluxUtil.sendMe(FileSystemMessageResult.class, url, param, EurekaConstants.READFILE);
        return result.message;
    }

    public Map<String, String> getMd5(Set<FileObject> f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fos = f;
        FileSystemStringResult result = WebFluxUtil.sendMe(FileSystemStringResult.class, url, param, EurekaConstants.GETMD5);
        return result.map;
    }

    private void configureParam(FileSystemParam param) {
        param.configname = controlService.getConfigName();
        param.configid = controlService.getConfigId();
        param.iconf = controlService.iconf;
        param.iserver = nodeConf.getInmemoryServer();
        if (Constants.REDIS.equals(nodeConf.getInmemoryServer())) {
            param.iconnection = nodeConf.getInmemoryRedis();
        }
    }

    public String getQueueName() {
        return null;
    }

    public void setQueue(String queueName) {
        this.queueName = queueName;
        this.queue =  new MyQueueFactory().create(queueName, nodeConf, controlService.curatorClient);
    }

    public void listFilesFullQueue(QueueElement element, FileObject fileObject) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fo = fileObject;
        element.setOpid(OperationConstants.LISTFILESFULL);
        element.setFileSystemFileObjectParam(param);
        queue.offer(element);
    }

    public void getMd5Queue(QueueElement element, FileObject fileObject) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fos = Set.of(fileObject);
        element.setOpid(OperationConstants.GETMD5);
        element.setFileSystemFileObjectParam(param);
        queue.offer(element);
    }

    public void readFileQueue(QueueElement element, FileObject fileObject) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        configureParam(param);
        param.fos = Set.of(fileObject);
        element.setOpid(OperationConstants.READFILE);
        element.setFileSystemFileObjectParam(param);
        queue.offer(element);
    }

}
