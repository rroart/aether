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
import roart.thread.ClientRunner;
import roart.model.FileLocation;
import roart.model.FileObject;
import roart.model.IndexFiles;
import roart.model.SearchDisplay;
import roart.queue.Queues;
import roart.config.ConfigConstants;
import roart.config.MyConfig;
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
    
    public ControlService() {
        startThreads();
    }
    
    private NodeConfig conf;
    
    public NodeConfig getConfig() {
        return conf;
    }
    
    public void getRemoteConfig() {
        ServiceParam param = new ServiceParam();
        ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.GETCONFIG);
        conf = result.config;
        MyConfig.conf = conf;
    }
    
    public void setRemoteConfig() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.webpath = EurekaConstants.SETCONFIG;
        Queues.clientQueue.add(param);
        return;           
    }
    
    public void configIndexing() {
        try {
            String index = null;
            if (conf.wantLucene()) {
                index = ConfigConstants.SEARCHENGINELUCENE;
            } else if (conf.wantSolr()) {
                index = ConfigConstants.SEARCHENGINESOLR;
            } else if (conf.wantElastic()) {
                index = ConfigConstants.SEARCHENGINEELASTIC;
            }
            if (index != null) {
                this.index = index;
            }
        } catch (Exception e) {
            // TODO propagate
            log.error(Constants.EXCEPTION, e); 
        }
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
        param.function = Function.FILESYSTEM;
        param.add = add;
        param.webpath = EurekaConstants.TRAVERSE;
        Queues.clientQueue.add(param);
        return;           
    }

    // called from ui
    // returns list: new file
    public void traverse() throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.FILESYSTEM;
        param.webpath = EurekaConstants.TRAVERSE;
        Queues.clientQueue.add(param);
    }

    static public String nodename = "localhost";
    public String index = null;
    public String db = null;
    
    // called from ui
    public void overlapping() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.OVERLAPPING;
        param.webpath = EurekaConstants.OVERLAPPING;
        Queues.clientQueue.add(param);
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
        param.webpath = EurekaConstants.INDEXSUFFIX;
        Queues.clientQueue.add(param);
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
        param.webpath = EurekaConstants.INDEX;
        Queues.clientQueue.add(param);
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
        param.webpath = EurekaConstants.REINDEXDATELOWER;
        Queues.clientQueue.add(param);
        return;           
    }

    public void reindexdatehigher(String date, boolean reindex) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.REINDEXDATE;
        param.higherdate = date;
        param.reindex = reindex;
        param.webpath = EurekaConstants.REINDEXDATEHIGHER;
        Queues.clientQueue.add(param);
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
        param.webpath = EurekaConstants.REINDEXLANGUAGE;
        Queues.clientQueue.add(param);
        return;           
    }

    // old, probably oudated by overlapping?
    public List<String> cleanupfs(String dirname) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.CONSISTENTCLEAN;
        param.dirname = dirname;
        param.webpath = EurekaConstants.CLEANUPFS;
        Queues.clientQueue.add(param);
        return null;           
    }

    // called from ui
    public void memoryusage() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.MEMORYUSAGE;
        param.webpath = EurekaConstants.MEMORYUSAGE;
        Queues.clientQueue.add(param);
        return;           
    }

    // called from ui
    // returns list: not indexed
    // returns list: another with columns
    public void notindexed() throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.NOTINDEXED;
        param.webpath = EurekaConstants.NOTINDEXED;
        Queues.clientQueue.add(param);
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
        param.function = Function.FILESYSTEMLUCENENEW;
        param.webpath = EurekaConstants.FILESYSTEMLUCENENEW;
        Queues.clientQueue.add(param);
        return;           
    }

    public void filesystemlucenenew(String add, boolean md5checknew) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.FILESYSTEMLUCENENEW;
        param.add = add;
        param.md5checknew = md5checknew;
        param.webpath = EurekaConstants.FILESYSTEMLUCENENEW;
        Queues.clientQueue.add(param);
        return;           
    }

    public void dbindex(String md5) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.DBINDEX;
        param.md5 = md5;
        param.webpath = EurekaConstants.DBINDEX;
        Queues.clientQueue.add(param);
        return;           
   }

    public void dbsearch(String md5) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.DBSEARCH;
        param.md5 = md5;
        param.webpath = EurekaConstants.DBSEARCH;
        Queues.clientQueue.add(param);
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
        param.webpath = EurekaConstants.CONSISTENTCLEAN;
        Queues.clientQueue.add(param);
        return;           
	    }

        public void deletepathdb(String path) {
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            //param.function = Function.DELETEPATHDB;
            param.path = path;
            param.webpath = EurekaConstants.DELETEPATHDB;
            Queues.clientQueue.add(param);
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
            param.webpath = EurekaConstants.SEARCHENGINE;
            Queues.clientQueue.add(param);
            return;           
       }
        
        public void machinelearning(String learning) {
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            param.name = learning;
            param.webpath = EurekaConstants.MACHINELEARNING;
            Queues.clientQueue.add(param);
            return;           
        }

        public void database(String db) {
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            param.name = db;
            param.webpath = EurekaConstants.DATABASE;
            Queues.clientQueue.add(param);
            return;           
        }

        public void filesystem(String fs) {
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            param.name = fs;
            param.webpath = EurekaConstants.FILESYSTEM;
            Queues.clientQueue.add(param);
            return;           
        }
        
        public String getAppName() {
            return EurekaConstants.AETHER;
        }
        
        private static ClientRunner clientRunnable = null;
        public static Thread clientWorker = null;

        public static void startThreads() {
            if (clientRunnable == null) {
                startClientWorker();
            }
        }

        public static void startClientWorker() {
            clientRunnable = new ClientRunner();
            clientWorker = new Thread(clientRunnable);
            clientWorker.setName("ClientWorker");
            clientWorker.start();
            //log.info("starting client worker");
        }

}
