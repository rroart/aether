package roart.config;

import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import roart.hcutil.GetHazelcastInstance;
import roart.lang.LanguageDetect;
import roart.service.ControlService;
import roart.util.Constants;
import roart.util.MyCollections;
import roart.util.MyHazelcastRemover;
import roart.util.MyMaps;
import roart.zkutil.ZKMessageUtil;

public class MyPropertyConfig extends MyConfig {

     public static MyConfig instance() {
        if (instance == null) {
            instance = new MyPropertyConfig();
        }
        return instance;
    }
    
    private static Configuration config = null;
    
    public MyPropertyConfig() {
        super();
	try {
         config = new PropertiesConfiguration(ConfigConstants.PROPFILE);
         //((AbstractConfiguration) config).setDelimiterParsingDisabled(true);
    } catch (ConfigurationException e) {
        log.error(Constants.EXCEPTION, e); 
    }
    }

    public static String[] fsvalues = { ConfigConstants.LOCAL, ConfigConstants.SWIFT, ConfigConstants.HADOOP };
    public static String[] lockmodevalues = { ConfigConstants.SMALL, ConfigConstants.BIG };
    
    @Override
    public NodeConfig mynode() {
        return getNode(ControlService.nodename);
    }

    @Override
    public void myput(String nodename, NodeConfig config) {
        nodemap.put(nodename, config);
    ZKMessageUtil.doreconfig(ControlService.nodename);
    }
    
    @Override
    public void config() {
        try {
            LanguageDetect.init("./profiles/");
        } catch (Exception e) {
            log.error("Exception", e);
        }
        
        String nodename  = getString(ConfigConstants.NODENAME, "localhost", false, false, null);
        if (nodename == null || nodename.length() == 0) {
            nodename = "localhost";
        }
        ControlService.nodename = nodename;
        
        String languages = getString(ConfigConstants.LANGUAGES, "en", false, false, null);
        conf.languages = languages.split(",");
        
        String fs = getString(ConfigConstants.FS, ConfigConstants.LOCAL, false, false, fsvalues); // dead?
        roart.filesystem.FileSystemDao.instance(fs);
        
        configDirlist();
        
        String index = getString(ConfigConstants.INDEX, ConfigConstants.SEARCHENGINELUCENE, false, false, ConfigConstants.indexvalues);
        configIndexing(index);

        configDb();
        
        if (conf.db.equals(ConfigConstants.DATABASEHIBERNATE) || conf.index.equals(ConfigConstants.SEARCHENGINELUCENE)) {
            ControlService.nodename = ConfigConstants.LOCALHOST; // force this
        }

        configHdfs();
        
        configSwift();
        
        String classify = getString(ConfigConstants.CLASSIFY, null, false, false, ConfigConstants.classifyvalues);
        configClassify(classify);
        
        configZoo();

        configLockmode();

        configDistributed();

        configMisc(); 
        
        configCurator();
        configRW();
        
        nodemap = MyMaps.get(ConfigConstants.CONFIG);
        nodemap.put(ControlService.nodename, conf);

    }

    private void configDistributed() {
        Boolean distributedtraverse = getBoolean(ConfigConstants.DISTRIBUTEDPROCESS, false, false, false);
        if (distributedtraverse != null && distributedtraverse.booleanValue() == true) {
            conf.distributedtraverse = true;
            GetHazelcastInstance.instance();
            conf.locker = roart.util.Constants.HAZELCAST;
            MyCollections.remover = new MyHazelcastRemover();
        } else {
            conf.distributedtraverse = false;
       }
    }

    private void configLockmode() {
        String zookeepermode = getString(ConfigConstants.DISTRIBUTEDLOCKMODE, ConfigConstants.BIG, false, false, lockmodevalues);
        if (zookeepermode != null && zookeepermode.equals(ConfigConstants.SMALL)) {
            conf.zookeepersmall = true;
        } else {
            conf.zookeepersmall = false;
        }
    }

    private void configZoo() {
        String zoo = getString(ConfigConstants.ZOOKEEPER, null, false, false, null);
        if (zoo != null && !ControlService.nodename.equals(ConfigConstants.LOCALHOST)) {
            conf.zookeeper = zoo;
            conf.locker = roart.util.Constants.CURATOR;
        }
    }

    private void configMisc() {
        boolean downloader = getBoolean(ConfigConstants.GUIDOWNLOADER, false, false, false);
        conf.downloader = downloader; 
        
        boolean authenticate = getBoolean(ConfigConstants.GUIAUTHENTICATE, false, false, false);
        conf.authenticate = authenticate;
    }

    private void configCurator() {
        if (roart.util.Constants.CURATOR.equals(conf.locker)) {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);        
            String zookeeperConnectionString = conf.zookeeper;
            ControlService.curatorClient = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
            ControlService.curatorClient.start();
        }
    }

    private void configRW() {
        for (NodeConfig.Config key : configDefaultMap.keySet()) {
            String keyStr = configStrMap.get(key);
            Integer deflt = configDefaultMap.get(key);
            Integer integer = getInteger(keyStr, deflt, false, false);
            conf.configMap.put(key, integer);
        }
    }

    public void configClassify(String classify) {
    	try {
        conf.classify = classify;
        if (classify != null && classify.equals(ConfigConstants.MAHOUT)) {
            String mahoutconffs = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTCONFFS, null, false, false, null);
            String mahoutbasepath = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTBASEPATH, null, false, false, null);
            String mahoutmodelPath = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTMODELPATH, null, true, true, null);
            String mahoutlabelIndexPath = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTLABELINDEXFILEPATH, null, true, true, null);
            String mahoutdictionaryPath = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDICTIONARYPATH, null, true, true, null);
            String mahoutdocumentFrequencyPath = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDOCUMENTFREQUENCYPATH, null, true, true, null);
            String mahoutbayestype = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTALGORITHM, null, true, true, null);
            conf.mahoutconffs = mahoutconffs;
            conf.mahoutbasepath = mahoutbasepath;
            conf.mahoutmodelpath = mahoutmodelPath;
            conf.mahoutlabelindexpath = mahoutlabelIndexPath;
            conf.mahoutdictionarypath = mahoutdictionaryPath;
            conf.mahoutdocumentfrequencypath = mahoutdocumentFrequencyPath;
            conf.mahoutalgorithm = mahoutbayestype;
        }
        if (classify != null && classify.equals(ConfigConstants.MAHOUTSPARK)) {
            String mahoutconffs = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTCONFFS, null, false, false, null);
            String mahoutbasepath = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTBASEPATH, null, false, false, null);
            String mahoutmodelPath = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTMODELPATH, null, true, true, null);
            String mahoutdictionaryPath = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDICTIONARYPATH, null, true, true, null);
            String mahoutdocumentFrequencyPath = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDOCUMENTFREQUENCYPATH, null, true, true, null);
            String mahoutbayestype = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTALGORITHM, null, true, true, null);
            String mahoutsparkmaster = getString(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTSPARKMASTER, null, true, true, null);
            conf.mahoutconffs = mahoutconffs;
            conf.mahoutbasepath = mahoutbasepath;
            conf.mahoutmodelpath = mahoutmodelPath;
            conf.mahoutdictionarypath = mahoutdictionaryPath;
            conf.mahoutdocumentfrequencypath = mahoutdocumentFrequencyPath;
            conf.mahoutalgorithm = mahoutbayestype;
            conf.mahoutsparkmaster = mahoutsparkmaster;
        }
        if (classify != null && classify.equals(ConfigConstants.MACHINELEARNINGSPARKML)) {
            String sparkmlbasepath = getString(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLBASEPATH, null, false, false, null);
            String sparkmlmodelPath = getString(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLMODELPATH, null, true, true, null);
            String sparkmllabelindexPath = getString(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLLABELINDEXPATH, null, true, true, null);
            String sparkmaster = getString(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMASTER, null, true, true, null);
            conf.sparkmlbasepath = sparkmlbasepath;
            conf.sparkmlmodelpath = sparkmlmodelPath;
            conf.sparkmllabelindexpath = sparkmllabelindexPath;
            conf.sparkmaster = sparkmaster;
        }
        if (classify != null && classify.equals(ConfigConstants.MACHINELEARNINGOPENNLP)) {
            String opennlpmodelpath = getString(ConfigConstants.MACHINELEARNINGOPENNLPOPENNLPMODELPATH, null, true, true, null);
            conf.opennlpmodelpath = opennlpmodelpath;
        }
        roart.classification.ClassifyDao.instance(classify);
        } catch (Exception e) {
        	// TODO propagate
            log.error(Constants.EXCEPTION, e); 
        }
    }

    private void configHdfs() {
        new roart.filesystem.LocalFileSystemAccess();
        String fsdefaultname = getString(ConfigConstants.FILESYSTEMHDFSHDFSCONFFS, null, false, false, null);
        if (fsdefaultname != null) {
            conf.hdfsdefaultname = fsdefaultname;
        }
    }

    private void configSwift() {
        new roart.filesystem.SwiftAccess();
        String swifturl = getString(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFURL, null, false, false, null);
        if (swifturl != null) {
            String swiftuser = getString(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFUSER, null, false, false, null);
            String swiftkey = getString(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFKEY, null, false, false, null);
            String swiftcontainer = getString(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFCONTAINER, null, false, false, null);
            conf.swifturl = swifturl;
            conf.swiftuser = swiftuser;
            conf.swiftkey = swiftkey;
            conf.swiftcontainer = swiftcontainer;
        }
    }

   private void configDb() {
        String db = getString(ConfigConstants.DB, ConfigConstants.DATABASEHIBERNATE, false, false, ConfigConstants.dbvalues);
        if (db.equals(ConfigConstants.DATABASEHBASE)) {
            String quorum = getString(ConfigConstants.DATABASEHBASEHBASEQUORUM, null, true, true, null);
            String port = getString(ConfigConstants.DATABASEHBASEHBASEPORT, null, true, true, null);
            String master = getString(ConfigConstants.DATABASEHBASEHBASEMASTER, null, true, true, null);
            conf.hbasequorum = quorum;
            conf.hbaseport = port;
            conf.hbasemaster = master;
        } else if (db.equals(ConfigConstants.DATABASEDATANUCLEUS)) {
        }
        conf.db = db;
        roart.database.IndexFilesDao.instance(db);
        conf.hasHibernate = db.equals(ConfigConstants.DATABASEHIBERNATE);
    }

    public void configIndexing(String index) {
    	try {
        if (index.equals(ConfigConstants.SEARCHENGINESOLR)) {
            String solrurl = getString(ConfigConstants.SEARCHENGINESOLRSOLRURL, null, true, true, null);
            conf.solrurl = solrurl;
        }
        if (index.equals(ConfigConstants.SEARCHENGINELUCENE)) {
            String lucenepath = getString(ConfigConstants.LUCENEPATH, null, true, true, null);
            conf.lucenepath = lucenepath;
        }
        if (index.equals(ConfigConstants.SEARCHENGINEELASTIC)) {
            String elastichost = getString(ConfigConstants.SEARCHENGINEELASTICELASTICHOST, null, true, true, null);
            String elasticport = getString(ConfigConstants.SEARCHENGINEELASTICELASTICPORT, null, true, true, null);
            conf.elastichost = elastichost;
            conf.elasticport = elasticport;
        }
        conf.index = index;
        Boolean storehighlight = getBoolean(ConfigConstants.GUIHIGHLIGHTMLT, false, false, false);
        if (storehighlight != null && storehighlight.booleanValue() == true) {
            conf.highlightmlt = true;
        }
        roart.search.SearchDao.instance(index);
    } catch (Exception e) {
    	// TODO propagate
        log.error(Constants.EXCEPTION, e); 
    }
    }

    private void configDirlist() {
        /*
        String dirliststr = getString(ConfigConstants.DIRLIST, null, true, true, null);
        String dirlistnotstr = getString(ConfigConstants.DIRLISTNOT, null, false, false, null);
        String[] dirlist = dirliststr.split(",");
        String[] dirlistnot = dirlistnotstr.split(",");
        System.out.println("dirliststr " + dirliststr);
        */
        String[] dirlist = getStringArray(ConfigConstants.FSDIRLIST, null, true, true);
        String[] dirlistnot = getStringArray(ConfigConstants.FSDIRLISTNOT, null, false, false);
        conf.dirlist = dirlist;
        conf.dirlistnot = dirlistnot;
    }

    public String getString(String string) {
        return config.getString(string);
    }
    
    public String[] getStringArray(String string) {
        return config.getStringArray(string);
    }
    
    public Boolean getBoolean(String string) {
        return config.getBoolean(string);
    }
    
    public Integer getInteger(String string) {
        return config.getInt(string);
    }
    
    public String getString(String key, String defaultvalue, boolean mandatory, boolean fatal, String [] legalvalues) {
        String value = null;
        try {
            value = getString(key);
        } catch (NoSuchElementException e) {
        } catch (ConversionException e) {
            if (fatal) {
                System.out.println("Can not convert config " + key);
                log.error("Can not convert config " + key);
                System.exit(0);
            } else {
                System.out.println("Can not convert config " + key);
                log.error("Can not convert config " + key);                
            }
        }
        if (value == null && mandatory) {
            System.out.println("Can not find mandatory config " + key);
            log.error("Can not find mandatory config " + key);
            System.exit(0);            
        }
        if (value == null) {
            value = defaultvalue;
        }
        boolean foundvalue = false;
        if (legalvalues != null) {
            for (String legalvalue : legalvalues) {
                if (legalvalue.equals(value)) {
                    foundvalue = true;
                    break;
                }
            }
        } else {
            foundvalue = true;
        }
        if (!foundvalue) {
            if (fatal || mandatory) {
                System.out.println("Illegal value " + key);
                log.error("Illegal value " + key);
                System.exit(0);                
            }
            if (defaultvalue != null) {
                log.error("Illegal value " + key + " = " + value + " setting to default " + defaultvalue);
                value = defaultvalue;
            } else {
                log.error("Ignoring illegal value " + key + " = " + value);
            }
        }
        return value;
    }
    
    public String[] getStringArray(String key, String[] defaultvalue, boolean mandatory, boolean fatal) {
        String value[] = null;
        try {
            value = getStringArray(key);
        } catch (NoSuchElementException e) {
        } catch (ConversionException e) {
            if (fatal) {
                System.out.println("Can not convert config " + key);
                log.error("Can not convert config " + key);
                System.exit(0);
            } else {
                System.out.println("Can not convert config " + key);
                log.error("Can not convert config " + key);                
            }
        }
        if (value == null && mandatory) {
            System.out.println("Can not find mandatory config " + key);
            log.error("Can not find mandatory config " + key);
            System.exit(0);            
        }
        if (value == null) {
            value = defaultvalue;
        }
        return value;
    }
    
    public Boolean getBoolean(String key, Boolean defaultvalue, boolean mandatory, boolean fatal) {
        Boolean value = null;
        try {
            value = getBoolean(key);
        } catch (NoSuchElementException e) {
        } catch (ConversionException e) {
            if (fatal) {
                System.out.println("Can not convert config " + key);
                log.error("Can not convert config " + key);
                System.exit(0);
            } else {
                System.out.println("Can not convert config " + key);
                log.error("Can not convert config " + key);                
            }
        }
        if (value == null && mandatory) {
            System.out.println("Can not find mandatory config " + key);
            log.error("Can not find mandatory config " + key);
            System.exit(0);            
        }
        if (value == null) {
            value = defaultvalue;
        }
        return value;
    }
    
    public Integer getInteger(String key, Integer defaultvalue, boolean mandatory, boolean fatal) {
        Integer value = null;
        try {
            value = getInteger(key);
        } catch (NoSuchElementException e) {
        } catch (ConversionException e) {
            if (fatal) {
                System.out.println("Can not convert config " + key);
                log.error("Can not convert config " + key);
                System.exit(0);
            } else {
                System.out.println("Can not convert config " + key);
                log.error("Can not convert config " + key);                
            }
        }
        if (value == null && mandatory) {
            System.out.println("Can not find mandatory config " + key);
            log.error("Can not find mandatory config " + key);
            System.exit(0);            
        }
        if (value == null) {
            value = defaultvalue;
        }           
        if (value < 0) {
            log.error("Illegal value " + key + " " + value + " setting to default " + defaultvalue);
            value = defaultvalue;
        }
        
        return value;
    }
    
}
