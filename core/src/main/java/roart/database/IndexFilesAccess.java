package roart.database;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import roart.service.ControlService;
import roart.common.config.MyConfig;
import roart.common.constants.EurekaConstants;
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
import roart.common.model.IndexFiles;
import roart.eureka.util.EurekaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.discovery.DiscoveryClient;

public abstract class IndexFilesAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DiscoveryClient discoveryClient;

    public abstract String getAppName();

    public String constructor() {
        DatabaseConstructorParam param = new DatabaseConstructorParam();
        param.setNodename(ControlService.nodename);
        param.setConf(MyConfig.conf);
        DatabaseConstructorResult result = EurekaUtil.sendMe(DatabaseConstructorResult.class, param, getAppName(), EurekaConstants.CONSTRUCTOR);
        return result.error;
    }

    public String destructor() {
        DatabaseConstructorParam param = new DatabaseConstructorParam();
        param.setNodename(ControlService.nodename);
        param.setConf(MyConfig.conf);
        DatabaseConstructorResult result = EurekaUtil.sendMe(DatabaseConstructorResult.class, param, getAppName(), EurekaConstants.DESTRUCTOR);
        return result.error;
    }

    public IndexFiles getByFilelocation(FileLocation fl) throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        param.setConf(MyConfig.conf);
        param.setFileLocation(fl);
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETBYFILELOCATION);
        return result.getIndexFiles()[0];
    }

    public String getMd5ByFilelocation(FileLocation fl) throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        param.setConf(MyConfig.conf);
        Set<FileLocation> fls = new HashSet<>();
        fls.add(fl);
        param.setFileLocations(fls);
        DatabaseMd5Result result = EurekaUtil.sendMe(DatabaseMd5Result.class, param, getAppName(), EurekaConstants.GETMD5BYFILELOCATION);
        return result.getMd5()[0];

    }

    public IndexFiles getByMd5(String md5) throws Exception {
        DatabaseMd5Param param = new DatabaseMd5Param();
        param.setConf(MyConfig.conf);
        Set<String> md5s = new HashSet<>();
        md5s.add(md5);
        param.setMd5s(md5s);
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETBYMD5);
        return result.getIndexFilesMap().get(md5);

    }

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
        DatabaseMd5Param param = new DatabaseMd5Param();
        param.setConf(MyConfig.conf);
        Set<String> md5s = new HashSet<>();
        md5s.add(md5);
        param.setMd5s(md5s);
        DatabaseFileLocationResult result = EurekaUtil.sendMe(DatabaseFileLocationResult.class, param, getAppName(), EurekaConstants.GETFILELOCATIONSBYMD5);
        return new HashSet(Arrays.asList(result.fileLocation));

    }

    public List<IndexFiles> getAll() throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        param.setConf(MyConfig.conf);
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETALL);
        return Arrays.asList(result.getIndexFiles());

    }

    public void save(IndexFiles i) throws Exception {
        DatabaseIndexFilesParam param = new DatabaseIndexFilesParam();
        param.setConf(MyConfig.conf);
        param.setIndexFiles(i);
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.SAVE);
    }

    public void flush() throws Exception {
        DatabaseParam param = new DatabaseMd5Param();
        param.setConf(MyConfig.conf);
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.FLUSH);
    }

    public void close() throws Exception {
        DatabaseParam param = new DatabaseMd5Param();
        param.setConf(MyConfig.conf);
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.CLOSE);
    }

    public void commit() throws Exception {
        // just any param
        DatabaseParam param = new DatabaseMd5Param(); 
        param.setConf(MyConfig.conf);
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.COMMIT);
    }

    public Set<String> getAllMd5() throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        param.setConf(MyConfig.conf);
        DatabaseMd5Result result = EurekaUtil.sendMe(DatabaseMd5Result.class, param, getAppName(), EurekaConstants.GETALLMD5);
        return new HashSet(Arrays.asList(result.getMd5()));
    }

    public Set<String> getLanguages() throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        param.setConf(MyConfig.conf);
        DatabaseLanguagesResult result = EurekaUtil.sendMe(DatabaseLanguagesResult.class, param, getAppName(), EurekaConstants.GETLANGUAGES);
        return new HashSet(Arrays.asList(result.languages));
    }

    public void delete(IndexFiles index) throws Exception {
        DatabaseIndexFilesParam param = new DatabaseIndexFilesParam();
        param.setConf(MyConfig.conf);
        param.setIndexFiles(index);
        EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.DELETE);
    }

    public Map<String, IndexFiles> getByMd5(Set<String> md5s) {
        DatabaseMd5Param param = new DatabaseMd5Param();
        param.setConf(MyConfig.conf);
        param.setMd5s(md5s);
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETBYMD5);
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
        param.setConf(MyConfig.conf);
        param.setFileLocations(fls);
        DatabaseMd5Result result = EurekaUtil.sendMe(DatabaseMd5Result.class, param, getAppName(), EurekaConstants.GETMD5BYFILELOCATION);
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
        param.setConf(MyConfig.conf);
        param.setFileLocations(fls);
        DatabaseMd5Result result = EurekaUtil.sendMe(DatabaseMd5Result.class, param, getAppName(), EurekaConstants.GETBOTHBYFILELOCATION);
        Map<String, String> fullMap = result.getMd5Map();
        Map<String, String> simpleMap = new HashMap<>();
        for (Entry<String, String> entry : fullMap.entrySet()) {
            String key = entry.getKey();
            simpleMap.put(key, entry.getValue());
        }
        return null; //simpleMap;
    }

}

