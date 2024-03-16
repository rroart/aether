package roart.database;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import roart.service.ControlService;
import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.constants.FileSystemConstants;
import roart.common.constants.OperationConstants;
import roart.common.database.DatabaseConstructorParam;
import roart.common.database.DatabaseConstructorResult;
import roart.common.database.DatabaseFileLocationParam;
import roart.common.database.DatabaseFileLocationResult;
import roart.common.database.DatabaseIndexFilesParam;
import roart.common.database.DatabaseIndexFilesResult;
import roart.common.database.DatabaseLanguagesResult;
import roart.common.database.DatabaseMd5Param;
import roart.common.database.DatabaseMd5Result;
import roart.common.database.DatabaseParam;
import roart.common.database.DatabaseResult;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.Files;
import roart.common.model.IndexFiles;
import roart.common.queue.QueueElement;
import roart.eureka.util.EurekaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IndexFilesAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private NodeConfig nodeConf;

    protected ControlService controlService;

    public IndexFilesAccess(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public abstract String getAppName();

    public abstract String getQueueName();
    
    public boolean queueWithAppId() {
        return false;
    }

    public String constructor() {
        DatabaseConstructorParam param = new DatabaseConstructorParam();
        configureParam(param);
        DatabaseConstructorResult result = EurekaUtil.sendMe(DatabaseConstructorResult.class, param, getAppName(), EurekaConstants.CONSTRUCTOR, nodeConf);
        return result.error;
    }

    public String destructor() {
        DatabaseConstructorParam param = new DatabaseConstructorParam();
        configureParam(param);
        DatabaseConstructorResult result = EurekaUtil.sendMe(DatabaseConstructorResult.class, param, getAppName(), EurekaConstants.DESTRUCTOR, nodeConf);
        return result.error;
    }

    public String clear() {
        DatabaseConstructorParam param = new DatabaseConstructorParam();
        configureParam(param);
        DatabaseConstructorResult result = EurekaUtil.sendMe(DatabaseConstructorResult.class, param, getAppName(), EurekaConstants.CLEAR, nodeConf);
        return result.error;
    }

    public String drop() {
        DatabaseConstructorParam param = new DatabaseConstructorParam();
        configureParam(param);
        DatabaseConstructorResult result = EurekaUtil.sendMe(DatabaseConstructorResult.class, param, getAppName(), EurekaConstants.DROP, nodeConf);
        return result.error;
    }
    
    // not used
    public IndexFiles getByFilelocation(FileLocation fl) throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        configureParam(param);
        param.setFileLocation(fl);
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETBYFILELOCATION, nodeConf);
        return result.getIndexFiles()[0];
    }

    // todo qu
    public String getMd5ByFilelocation(FileLocation fl) throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        configureParam(param);
        Set<FileLocation> fls = new HashSet<>();
        fls.add(fl);
        param.setFileLocations(fls);
        DatabaseMd5Result result = EurekaUtil.sendMe(DatabaseMd5Result.class, param, getAppName(), EurekaConstants.GETMD5BYFILELOCATION, nodeConf);
        return result.getMd5()[0];

    }

    public IndexFiles getByMd5(String md5) throws Exception {
        DatabaseMd5Param param = new DatabaseMd5Param();
        configureParam(param);
        Set<String> md5s = new HashSet<>();
        md5s.add(md5);
        param.setMd5s(md5s);
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETBYMD5, nodeConf);
        return result.getIndexFilesMap().get(md5);

    }

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
        DatabaseMd5Param param = new DatabaseMd5Param();
        configureParam(param);
        /*
        Set<String> md5s = new HashSet<>();
        md5s.add(md5);
        param.setMd5s(md5s);
        */
        param.setMd5(md5);
        DatabaseFileLocationResult result = EurekaUtil.sendMe(DatabaseFileLocationResult.class, param, getAppName(), EurekaConstants.GETFILELOCATIONSBYMD5, nodeConf);
        return new HashSet(Arrays.asList(result.fileLocation));

    }

    public List<IndexFiles> getAll() throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        configureParam(param);
        long time = System.currentTimeMillis();
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETALL, nodeConf);
        log.info("Load time {} for {}", (System.currentTimeMillis() - time) / 1000, result.getIndexFiles().length);
        return Arrays.asList(result.getIndexFiles());

    }

    public List<Files> getAllFiles() throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        configureParam(param);
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETALLFILES, nodeConf);
        return Arrays.asList(result.getFiles());

    }

    public void save(Set<IndexFiles> saves) throws Exception {
        DatabaseIndexFilesParam param = new DatabaseIndexFilesParam();
        configureParam(param);
        param.setIndexFiles(saves);
        long time = System.currentTimeMillis();
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.SAVE, nodeConf);
        if (!saves.isEmpty()) {
            log.info("Save time {} for {}", (System.currentTimeMillis() - time) / 1000, saves.size());
        }
    }

    public void flush() throws Exception {
        DatabaseParam param = new DatabaseMd5Param();
        configureParam(param);
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.FLUSH, nodeConf);
    }

    public void close() throws Exception {
        DatabaseParam param = new DatabaseMd5Param();
        configureParam(param);
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.CLOSE, nodeConf);
    }

    public void commit() throws Exception {
        // just any param
        DatabaseParam param = new DatabaseMd5Param(); 
        configureParam(param);
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.COMMIT, nodeConf);
    }

    public Set<String> getAllMd5() throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        configureParam(param);
        DatabaseMd5Result result = EurekaUtil.sendMe(DatabaseMd5Result.class, param, getAppName(), EurekaConstants.GETALLMD5, nodeConf);
        return new HashSet(Arrays.asList(result.getMd5()));
    }

    public Set<String> getLanguages() throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        configureParam(param);
        DatabaseLanguagesResult result = EurekaUtil.sendMe(DatabaseLanguagesResult.class, param, getAppName(), EurekaConstants.GETLANGUAGES, nodeConf);
        return new HashSet(Arrays.asList(result.languages));
    }

    public void delete(IndexFiles index) throws Exception {
        DatabaseIndexFilesParam param = new DatabaseIndexFilesParam();
        configureParam(param);
        Set<IndexFiles> indexes = new HashSet<>();
        indexes.add(index);
        param.setIndexFiles(indexes);
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.DELETE, nodeConf);
    }

    public void delete(Files index) throws Exception {
        DatabaseIndexFilesParam param = new DatabaseIndexFilesParam();
        configureParam(param);
        Set<Files> indexes = new HashSet<>();
        indexes.add(index);
        param.setFiles(indexes);
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.DELETE, nodeConf);
    }

    public Map<String, IndexFiles> getByMd5(Set<String> md5s) {
        DatabaseMd5Param param = new DatabaseMd5Param();
        configureParam(param);
        param.setMd5s(md5s);
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETBYMD5, nodeConf);
        Map<String, IndexFiles> fullMap = result.getIndexFilesMap();
        Map<String, IndexFiles> simpleMap = new HashMap<>();
        for (Entry<String, IndexFiles> entry : fullMap.entrySet()) {
            String key = entry.getKey();
            simpleMap.put(key, entry.getValue());
        }
        return simpleMap;
    }

    public Map<String, String> getMd5ByFilelocation(Set<FileLocation> fls) throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        configureParam(param);
        param.setFileLocations(fls);
        DatabaseMd5Result result = EurekaUtil.sendMe(DatabaseMd5Result.class, param, getAppName(), EurekaConstants.GETMD5BYFILELOCATION, nodeConf);
        Map<String, String> fullMap = result.getMd5Map();
        Map<String, String> simpleMap = new HashMap<>();
        for (Entry<String, String> entry : fullMap.entrySet()) {
            String key = entry.getKey();
            simpleMap.put(key, entry.getValue());
        }
        return simpleMap;
    }

    public List<Map> getBothByFilelocation(Set<FileLocation> fls) {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        configureParam(param);
        param.setFileLocations(fls);
        DatabaseMd5Result result = EurekaUtil.sendMe(DatabaseMd5Result.class, param, getAppName(), EurekaConstants.GETBOTHBYFILELOCATION, nodeConf);
        Map<String, String> fullMap = result.getMd5Map();
        Map<String, String> simpleMap = new HashMap<>();
        for (Entry<String, String> entry : fullMap.entrySet()) {
            String key = entry.getKey();
            simpleMap.put(key, entry.getValue());
        }
        return null; //simpleMap;
    }

    private void configureParam(DatabaseParam param) {
        param.setConfigname(controlService.getConfigName());
        param.setConfigid(controlService.getConfigId());
        param.setIconf(controlService.iconf);
        param.setIserver(nodeConf.getInmemoryServer());
        if (Constants.REDIS.equals(nodeConf.getInmemoryServer())) {
            param.setIconnection(nodeConf.getInmemoryRedis());
        }
    }
    
    public void setQueue(String queueName) {
        MyQueue<QueueElement> queue =  new MyQueueFactory().create(queueName, nodeConf, controlService.curatorClient);
    }

    public MyQueue<QueueElement> getQueue(FileObject fileObject) {
        String appId = queueWithAppId() && System.getenv("APPID") != null ? System.getenv("APPID") : "";
        String queueName = getQueueName() + appId;
        return new MyQueueFactory().create(queueName, nodeConf, controlService.curatorClient);
    }

    public void getByMd5Queue(QueueElement element, Set<String> md5s) {
        DatabaseMd5Param param = new DatabaseMd5Param();
        configureParam(param);
        param.setMd5s(md5s);
        element.setOpid(OperationConstants.GETBYMD5);
        element.setDatabaseMd5Param(param);
        getQueue(element.getFileObject()).offer(element);
    }

    public void getMd5ByFilelocationQueue(QueueElement element, Set<FileLocation> fls) throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        configureParam(param);
        param.setFileLocations(fls);
        element.setOpid(OperationConstants.GETMD5BYFILELOCATION);
        element.setDatabaseFileLocationParam(param);
        getQueue(element.getFileObject()).offer(element);
    }

}

