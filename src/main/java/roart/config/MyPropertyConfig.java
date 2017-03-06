package roart.config;

import java.util.NoSuchElementException;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import roart.filesystem.HDFS;
import roart.filesystem.Swift;
import roart.hcutil.GetHazelcastInstance;
import roart.lang.LanguageDetect;
import roart.service.ControlService;
import roart.util.Constants;
import roart.util.MyCollections;
import roart.util.MyHazelcastRemover;
import roart.util.MyMaps;
import roart.util.Prop;

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

    public static String[] fsvalues = { ConfigConstants.LOCAL, ConfigConstants.HADOOP };
    public static String[] indexvalues = { ConfigConstants.LUCENE, ConfigConstants.SOLR, ConfigConstants.ELASTIC };
    public static String[] dbvalues = { ConfigConstants.HIBERNATE, ConfigConstants.DATANUCLEUS, ConfigConstants.HBASE };
    public static String[] classifyvalues = { ConfigConstants.MAHOUT, ConfigConstants.MAHOUTSPARK, ConfigConstants.SPARKML, ConfigConstants.OPENNLP };
    public static String[] lockmodevalues = { ConfigConstants.SMALL, ConfigConstants.BIG };
    
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
        conf.languages = languages;
        
        String fs = getString(ConfigConstants.FS, ConfigConstants.LOCAL, false, false, fsvalues); // dead?
        roart.filesystem.FileSystemDao.instance(fs);
        
        configDirlist();
        
        configIndexing();

        configDb();
        
        if (conf.db.equals(ConfigConstants.HIBERNATE) || conf.index.equals(ConfigConstants.LUCENE)) {
            ControlService.nodename = ConfigConstants.LOCALHOST; // force this
        }

        configHdfs();
        
        configSwift();
        
        configClassify();
        
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
        boolean downloader = getBoolean(ConfigConstants.DOWNLOADER, false, false, false);
        conf.downloader = downloader; 
        
        boolean authenticate = getBoolean(ConfigConstants.AUTHENTICATE, false, false, false);
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
        for (Config key : configDefaultMap.keySet()) {
            String keyStr = configStrMap.get(key);
            Integer deflt = configDefaultMap.get(key);
            Integer integer = getInteger(keyStr, deflt, false, false);
            conf.configMap.put(key, integer);
        }
    }

    private void configClassify() {
        String classify = getString(ConfigConstants.CLASSIFY, null, false, false, classifyvalues);
        conf.classify = classify;
        if (classify != null && classify.equals(ConfigConstants.MAHOUT)) {
            String mahoutconffs = getString(ConfigConstants.MAHOUTCONFFS, null, false, false, null);
            String mahoutbasepath = getString(ConfigConstants.MAHOUTBASEPATH, null, false, false, null);
            String mahoutmodelPath = getString(ConfigConstants.MAHOUTMODELPATH, null, true, true, null);
            String mahoutlabelIndexPath = getString(ConfigConstants.MAHOUTLABELINDEXFILEPATH, null, true, true, null);
            String mahoutdictionaryPath = getString(ConfigConstants.MAHOUTDICTIONARYPATH, null, true, true, null);
            String mahoutdocumentFrequencyPath = getString(ConfigConstants.MAHOUTDOCUMENTFREQUENCYPATH, null, true, true, null);
            String mahoutbayestype = getString(ConfigConstants.MAHOUTALGORITHM, null, true, true, null);
            conf.mahoutconffs = mahoutconffs;
            conf.mahoutbasepath = mahoutbasepath;
            conf.mahoutmodelpath = mahoutmodelPath;
            conf.mahoutlabelindexpath = mahoutlabelIndexPath;
            conf.mahoutdictionarypath = mahoutdictionaryPath;
            conf.mahoutdocumentfrequencypath = mahoutdocumentFrequencyPath;
            conf.mahoutalgorithm = mahoutbayestype;
            new roart.classification.MahoutClassify();
        }
        if (classify != null && classify.equals(ConfigConstants.MAHOUTSPARK)) {
            String mahoutconffs = getString(ConfigConstants.MAHOUTCONFFS, null, false, false, null);
            String mahoutbasepath = getString(ConfigConstants.MAHOUTBASEPATH, null, false, false, null);
            String mahoutmodelPath = getString(ConfigConstants.MAHOUTMODELPATH, null, true, true, null);
            String mahoutdictionaryPath = getString(ConfigConstants.MAHOUTDICTIONARYPATH, null, true, true, null);
            String mahoutdocumentFrequencyPath = getString(ConfigConstants.MAHOUTDOCUMENTFREQUENCYPATH, null, true, true, null);
            String mahoutbayestype = getString(ConfigConstants.MAHOUTALGORITHM, null, true, true, null);
            String mahoutsparkmaster = getString(ConfigConstants.MAHOUTSPARKMASTER, null, true, true, null);
            conf.mahoutconffs = mahoutconffs;
            conf.mahoutbasepath = mahoutbasepath;
            conf.mahoutmodelpath = mahoutmodelPath;
            conf.mahoutdictionarypath = mahoutdictionaryPath;
            conf.mahoutdocumentfrequencypath = mahoutdocumentFrequencyPath;
            conf.mahoutalgorithm = mahoutbayestype;
            conf.mahoutsparkmaster = mahoutsparkmaster;
            new roart.classification.MahoutSparkClassify();
        }
        if (classify != null && classify.equals(ConfigConstants.SPARKML)) {
            String sparkmlmodelPath = getString(ConfigConstants.SPARKMLMODELPATH, null, true, true, null);
            String sparkmllabelindexPath = getString(ConfigConstants.SPARKMLLABELINDEXPATH, null, true, true, null);
            String sparkmaster = getString(ConfigConstants.SPARKMASTER, null, true, true, null);
            conf.sparkmlmodelpath = sparkmlmodelPath;
            conf.sparkmllabelindexpath = sparkmllabelindexPath;
            conf.sparkmaster = sparkmaster;
            new roart.classification.SparkMLClassify();
        }
        if (classify != null && classify.equals(ConfigConstants.OPENNLP)) {
            String opennlpmodelpath = getString(ConfigConstants.OPENNLPMODELPATH, null, true, true, null);
            conf.opennlpmodelpath = opennlpmodelpath;
            new roart.classification.OpennlpClassify();
        }
        roart.classification.ClassifyDao.instance(classify);
    }

    private void configHdfs() {
        new roart.filesystem.LocalFileSystemAccess();
        String fsdefaultname = getString(ConfigConstants.HDFSCONFFS, null, false, false, null);
        if (fsdefaultname != null) {
            conf.hdfsdefaultname = fsdefaultname;
            new HDFS();
        }
    }

    private void configSwift() {
        new roart.filesystem.SwiftAccess();
        String swifturl = getString(ConfigConstants.SWIFTCONFURL, null, false, false, null);
        if (swifturl != null) {
            String swiftuser = getString(ConfigConstants.SWIFTCONFUSER, null, false, false, null);
            String swiftkey = getString(ConfigConstants.SWIFTCONFKEY, null, false, false, null);
            String swiftcontainer = getString(ConfigConstants.SWIFTCONFCONTAINER, null, false, false, null);
            conf.swifturl = swifturl;
            conf.swiftuser = swiftuser;
            conf.swiftkey = swiftkey;
            conf.swiftcontainer = swiftcontainer;
            new Swift();
        }
    }

   private void configDb() {
        String db = getString(ConfigConstants.DB, ConfigConstants.HIBERNATE, false, false, dbvalues);
        if (db.equals(ConfigConstants.HBASE)) {
            String quorum = getString(ConfigConstants.HBASEQUORUM, null, true, true, null);
            String port = getString(ConfigConstants.HBASEPORT, null, true, true, null);
            String master = getString(ConfigConstants.HBASEMASTER, null, true, true, null);
            conf.hbasequorum = quorum;
            conf.hbaseport = port;
            conf.hbasemaster = master;
            new roart.database.HbaseIndexFiles();
        } else if (db.equals(ConfigConstants.DATANUCLEUS)) {
            new roart.database.DataNucleusIndexFiles();
        }
        conf.db = db;
        roart.database.IndexFilesDao.instance(db);
        conf.hasHibernate = db.equals(ConfigConstants.HIBERNATE);
    }

    private void configIndexing() {
        String index = getString(ConfigConstants.INDEX, ConfigConstants.LUCENE, false, false, indexvalues);
        if (index.equals(ConfigConstants.SOLR)) {
            String solrurl = getString(ConfigConstants.SOLRURL, null, true, true, null);
            conf.solrurl = solrurl;
            new roart.search.SearchSolr();
        }
        if (index.equals(ConfigConstants.LUCENE)) {
            String lucenepath = getString(ConfigConstants.LUCENEPATH, null, true, true, null);
            conf.lucenepath = lucenepath;
            org.apache.lucene.search.BooleanQuery.setMaxClauseCount(16384);
        }
        if (index.equals(ConfigConstants.ELASTIC)) {
            String elastichost = getString(ConfigConstants.ELASTICHOST, null, true, true, null);
            String elasticport = getString(ConfigConstants.ELASTICPORT, null, true, true, null);
            conf.elastichost = elastichost;
            conf.elasticport = elasticport;
            new roart.search.SearchElastic();
        }
        conf.index = index;
        Boolean storehighlight = getBoolean(ConfigConstants.HIGHLIGHTMLT, false, false, false);
        if (storehighlight != null && storehighlight.booleanValue() == true) {
            conf.highlightmlt = true;
        }
        roart.search.SearchDao.instance(index);
    }

    private void configDirlist() {
        /*
        String dirliststr = getString(ConfigConstants.DIRLIST, null, true, true, null);
        String dirlistnotstr = getString(ConfigConstants.DIRLISTNOT, null, false, false, null);
        String[] dirlist = dirliststr.split(",");
        String[] dirlistnot = dirlistnotstr.split(",");
        System.out.println("dirliststr " + dirliststr);
        */
        String[] dirlist = getStringArray(ConfigConstants.DIRLIST, null, true, true);
        String[] dirlistnot = getStringArray(ConfigConstants.DIRLISTNOT, null, false, false);
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
