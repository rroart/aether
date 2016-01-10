package roart.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import roart.config.MyConfig.Config;
import roart.service.ControlService;
import roart.util.MyMap;
import roart.util.MyMaps;
import roart.util.MySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MyConfig {

    protected static Logger log = LoggerFactory.getLogger(MyConfig.class);
    
    protected static MyConfig instance = null;
    
    public static MyConfig instance() {
        return instance;
    }
    
    public static NodeConfig conf = null;
    
    public MyConfig() {
        conf = new NodeConfig();
        createMaps();
    }
    
    MyMap nodemap = null;
    
    public NodeConfig mynode() {
        return getNode(ControlService.nodename);
    }
    
    public NodeConfig getNode(String nodename) {
        Map<String, NodeConfig> set = nodemap.getAll();
        return set.get(nodename);
    }
    
    public Set<String> getNodes() {
        Set<String> set = nodemap.getAll().keySet();
        return set;
    }
    
    public enum Config { REINDEXLIMIT, INDEXLIMIT, FAILEDLIMIT, OTHERTIMEOUT, TIKATIMEOUT, MLTCOUNT, MLTMINTF, MLTMINDF }
    public static Map<Config, Integer> configDefaultMap = new HashMap<Config, Integer>();
    public static Map<Config, String> configStrMap = new HashMap<Config, String>();
    //public static volatile String zookeeper = null;
    //public static boolean zookeepersmall = false;
    //public static boolean distributedtraverse = false;
    //public static String locker = null; // null, curator, zk, hz
    //public static MyLock lock;
    
    public abstract void config();

    public static void createMaps() {
        configDefaultMap.put(Config.FAILEDLIMIT, ConfigConstants.DEFAULT_CONFIG_FAILEDLIMIT);
        configDefaultMap.put(Config.TIKATIMEOUT, ConfigConstants.DEFAULT_CONFIG_TIKATIMEOUT);
        configDefaultMap.put(Config.OTHERTIMEOUT, ConfigConstants.DEFAULT_CONFIG_OTHERTIMEOUT);
        configDefaultMap.put(Config.INDEXLIMIT, ConfigConstants.DEFAULT_CONFIG_INDEXLIMIT);
        configDefaultMap.put(Config.REINDEXLIMIT, ConfigConstants.DEFAULT_CONFIG_REINDEXLIMIT);
    
        // solr defaults
        configDefaultMap.put(Config.MLTCOUNT, ConfigConstants.DEFAULT_CONFIG_MLTCOUNT);
        configDefaultMap.put(Config.MLTMINDF, ConfigConstants.DEFAULT_CONFIG_MLTMINDF);
        configDefaultMap.put(Config.MLTMINTF, ConfigConstants.DEFAULT_CONFIG_MLTMINTF);
        
        configStrMap.put(Config.FAILEDLIMIT, ConfigConstants.FAILEDLIMIT);
        configStrMap.put(Config.TIKATIMEOUT, ConfigConstants.TIKATIMEOUT);
        configStrMap.put(Config.OTHERTIMEOUT, ConfigConstants.OTHERTIMEOUT);
        configStrMap.put(Config.INDEXLIMIT, ConfigConstants.INDEXLIMIT);
        configStrMap.put(Config.REINDEXLIMIT, ConfigConstants.REINDEXLIMIT);
    
        configStrMap.put(Config.MLTCOUNT, ConfigConstants.MLTCOUNT);
        configStrMap.put(Config.MLTMINDF, ConfigConstants.MLTMINDF);
        configStrMap.put(Config.MLTMINTF, ConfigConstants.MLTMINTF);
    }
    
}
