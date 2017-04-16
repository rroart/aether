package roart.service;

import roart.model.ResultItem;

import javax.servlet.http.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.io.*;

import roart.service.ServiceParam.Function;
import roart.model.FileLocation;
import roart.model.FileObject;
import roart.model.IndexFiles;
import roart.model.SearchDisplay;
import roart.config.ConfigConstants;
import roart.config.NodeConfig;
import roart.database.DatabaseLanguagesResult;
import roart.util.Constants;
import roart.util.EurekaConstants;
import roart.util.EurekaUtil;
import roart.util.MyLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static volatile Integer writelock = new Integer(-1);

    private static int dirsizelimit = 100;

    private static volatile int mycounter = 0;
    
    private NodeConfig conf;
    
    private NodeConfig getConfig() {
        return conf;
    }
    
    public void getRemoteConfig() {
        ServiceParam param = new ServiceParam();
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.GETCONFIG);
        conf = result.config;
    }
    
    public void setRemoteConfig() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.SETCONFIG);
        return;           
    }
    
    /*
    public static int getMyCounter() {
    }
    
    public static String getMyId() {
    }
    */
    
    // called from ui
    // returns list: new file
    public void traverse(String add) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.add = add;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.TRAVERSE);
        return;           
    }

    // called from ui
    // returns list: new file
    public void traverse() throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.TRAVERSE);
    }

    static public String nodename = "localhost";
    
    // called from ui
    public void overlapping() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.OVERLAPPING);
        return;           
   }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: not indexed
    // returns list: deleted
    public void indexsuffix(String suffix, boolean reindex) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.REINDEXSUFFIX;
        param.suffix = suffix;
        param.reindex = reindex;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.INDEXSUFFIX);
        return;           
    }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: file does not exist
    // returns list: not indexed
    public void index(String add, boolean reindex) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.INDEX;
        param.add = add;
        param.reindex = reindex;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.INDEX);
        return;           
    }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: not indexed
    public void reindexdatelower(String date, boolean reindex) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.REINDEXDATE;
        param.lowerdate = date;
        param.reindex = reindex;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.REINDEXDATELOWER);
        return;           
    }

    public void reindexdatehigher(String date, boolean reindex) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.REINDEXDATE;
        param.higherdate = date;
        param.reindex = reindex;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.REINDEXDATEHIGHER);
        return;           
   }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: file does not exist
    // returns list: not indexed
    public void reindexlanguage(String lang) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.REINDEXLANGUAGE;
        param.lang = lang;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.REINDEXLANGUAGE);
        return;           
    }

    // old, probably oudated by overlapping?
    public List<String> cleanupfs(String dirname) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.dirname = dirname;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.CLEANUPFS);
        return null;           
    }

    // called from ui
    public void memoryusage() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.MEMORYUSAGE);
        return;           
    }

    // called from ui
    // returns list: not indexed
    // returns list: another with columns
    public void notindexed() throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.NOTINDEXED);
        return;           
    }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: new file
    // returns list: file does not exist
    // returns list: not indexed
    public void filesystemlucenenew() throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.FILESYSTEMLUCENENEW);
        return;           
    }

    public void filesystemlucenenew(String add, boolean md5checknew) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.add = add;
        param.md5checknew = md5checknew;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.FILESYSTEMLUCENENEW);
        return;           
    }

    public void dbindex(String md5) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.md5 = md5;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.DBINDEX);
        return;           
   }

    public void dbsearch(String md5) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.md5 = md5;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.DBSEARCH);
        return;           
    }

    @SuppressWarnings("rawtypes")
	private List<List> mergeListSet(Set<List> listSet, int size) {
	List<List> retlistlist = new ArrayList<List>();
	return retlistlist;
    }

	public void consistentclean(boolean clean) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
	param.function = Function.CONSISTENTCLEAN;
	    param.clean = clean;
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.CONSISTENTCLEAN);
        return;           
	    }

        public void deletepathdb(String path) {
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            //param.function = Function.DELETEPATHDB;
            param.path = path;
            ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.DELETEPATHDB);
            return;           
       }

        public Set<String> getLanguages() {
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            //param.function = Function.DELETEPATHDB;
            DatabaseLanguagesResult result = EurekaUtil.sendMe(DatabaseLanguagesResult.class, param, getAppName(), EurekaConstants.GETLANGUAGES);
            return new HashSet(Arrays.asList(result.languages));        
       }

        public void searchengine(String engine) {
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            param.name = engine;
            ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.SEARCHENGINE);
            return;           
       }
        
        public void machinelearning(String learning) {
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            param.name = learning;
            ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.MACHINELEARNING);
            return;           
        }

        public void database(String db) {
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            param.name = db;
            ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.DATABASE);
            return;           
        }

        public void filesystem(String fs) {
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            param.name = fs;
            ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.FILESYSTEM);
            return;           
        }
        
        public String getAppName() {
            return EurekaConstants.AETHER;
        }
        
}
