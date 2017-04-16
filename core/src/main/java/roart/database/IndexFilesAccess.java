package roart.database;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roart.model.IndexFiles;
import roart.service.ControlService;
import roart.util.EurekaConstants;
import roart.util.EurekaUtil;
import roart.config.MyConfig;
import roart.model.FileLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.discovery.DiscoveryClient;

public abstract class IndexFilesAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DiscoveryClient discoveryClient;

    public abstract String getAppName();
    
    public String constructor() {
        DatabaseConstructorParam param = new DatabaseConstructorParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        DatabaseConstructorResult result = EurekaUtil.sendMe(DatabaseConstructorResult.class, param, getAppName(), EurekaConstants.CONSTRUCTOR);
        return result.error;
    }
    
    public String destructor() {
        DatabaseConstructorParam param = new DatabaseConstructorParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        DatabaseConstructorResult result = EurekaUtil.sendMe(DatabaseConstructorResult.class, param, getAppName(), EurekaConstants.DESTRUCTOR);
        return result.error;
    }
    
    public IndexFiles getByFilelocation(FileLocation fl) throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        param.conf = MyConfig.conf;
        param.fileLocation = fl;
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETBYFILELOCATION);
        return result.indexFiles[0];
    }
    
    public String getMd5ByFilelocation(FileLocation fl) throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        param.conf = MyConfig.conf;
        param.fileLocation = fl;
        DatabaseMd5Result result = EurekaUtil.sendMe(DatabaseMd5Result.class, param, getAppName(), EurekaConstants.GETMD5BYFILELOCATION);
        return result.md5[0];
        
    }

    public IndexFiles getByMd5(String md5) throws Exception {
        DatabaseMd5Param param = new DatabaseMd5Param();
        param.conf = MyConfig.conf;
        param.md5 = md5;
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETBYMD5);
        return result.indexFiles[0];
        
    }

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
        DatabaseMd5Param param = new DatabaseMd5Param();
        param.conf = MyConfig.conf;
        param.md5 = md5;
        DatabaseFileLocationResult result = EurekaUtil.sendMe(DatabaseFileLocationResult.class, param, getAppName(), EurekaConstants.GETFILELOCATIONSBYMD5);
        return new HashSet(Arrays.asList(result.fileLocation));

    }

    public List<IndexFiles> getAll() throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        param.conf = MyConfig.conf;
        DatabaseIndexFilesResult result = EurekaUtil.sendMe(DatabaseIndexFilesResult.class, param, getAppName(), EurekaConstants.GETALL);
        return Arrays.asList(result.indexFiles);
       
    }

    public void save(IndexFiles i) throws Exception {
        DatabaseIndexFilesParam param = new DatabaseIndexFilesParam();
        param.conf = MyConfig.conf;
        param.indexFiles = i;
        DatabaseResult result = EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.SAVE);
        return;

    }

    public void flush() throws Exception {
        DatabaseParam param = new DatabaseMd5Param();
        param.conf = MyConfig.conf;
        DatabaseResult result = EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.FLUSH);
        return;
       
    }

    public void close() throws Exception {
        DatabaseParam param = new DatabaseMd5Param();
        param.conf = MyConfig.conf;
        DatabaseResult result = EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.CLOSE);
        return;
       
    }

    public void commit() throws Exception {
        // just any param
        DatabaseParam param = new DatabaseMd5Param(); 
        param.conf = MyConfig.conf;
        DatabaseResult result = EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.COMMIT);
        return;
        
    }

	public Set<String> getAllMd5() throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        param.conf = MyConfig.conf;
        DatabaseMd5Result result = EurekaUtil.sendMe(DatabaseMd5Result.class, param, getAppName(), EurekaConstants.GETALLMD5);
        return new HashSet(Arrays.asList(result.md5));
	    
	}

	public Set<String> getLanguages() throws Exception {
        DatabaseFileLocationParam param = new DatabaseFileLocationParam();
        param.conf = MyConfig.conf;
        DatabaseLanguagesResult result = EurekaUtil.sendMe(DatabaseLanguagesResult.class, param, getAppName(), EurekaConstants.GETLANGUAGES);
        return new HashSet(Arrays.asList(result.languages));
	    
	}

    public void delete(IndexFiles index) throws Exception {
        DatabaseIndexFilesParam param = new DatabaseIndexFilesParam();
        param.conf = MyConfig.conf;
        param.indexFiles = index;
        DatabaseResult result = EurekaUtil.sendMe(DatabaseResult.class, param, getAppName(), EurekaConstants.DELETE);
        return;
        
    }

    //public abstract IndexFiles ensureExistence(String md5) throws Exception;

}

