package roart.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import roart.config.NodeConfig;
import roart.service.ControlService;
import roart.util.MyMap;
import roart.util.MyMaps;
import roart.util.MySet;
import roart.zkutil.ZKMessageUtil;

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
    
    public void myput(String nodename, NodeConfig config) {
        nodemap.put(nodename, config);
	ZKMessageUtil.doreconfig();
    }
    
    public void reconfig() {
        conf = mynode();
    }
    
    public static Map<NodeConfig.Config, Integer> configDefaultMap = new HashMap<NodeConfig.Config, Integer>();
    public static Map<NodeConfig.Config, String> configStrMap = new HashMap<NodeConfig.Config, String>();
    //public static volatile String zookeeper = null;
    //public static boolean zookeepersmall = false;
    //public static boolean distributedtraverse = false;
    //public static String locker = null; // null, curator, zk, hz
    //public static MyLock lock;
    
    public abstract void config();

    public static void createMaps() {
        configDefaultMap.put(NodeConfig.Config.FAILEDLIMIT, ConfigConstants.DEFAULT_CONFIG_FAILEDLIMIT);
        configDefaultMap.put(NodeConfig.Config.TIKATIMEOUT, ConfigConstants.DEFAULT_CONFIG_TIKATIMEOUT);
        configDefaultMap.put(NodeConfig.Config.OTHERTIMEOUT, ConfigConstants.DEFAULT_CONFIG_OTHERTIMEOUT);
        configDefaultMap.put(NodeConfig.Config.INDEXLIMIT, ConfigConstants.DEFAULT_CONFIG_INDEXLIMIT);
        configDefaultMap.put(NodeConfig.Config.REINDEXLIMIT, ConfigConstants.DEFAULT_CONFIG_REINDEXLIMIT);
    
        // solr defaults
        configDefaultMap.put(NodeConfig.Config.MLTCOUNT, ConfigConstants.DEFAULT_CONFIG_MLTCOUNT);
        configDefaultMap.put(NodeConfig.Config.MLTMINDF, ConfigConstants.DEFAULT_CONFIG_MLTMINDF);
        configDefaultMap.put(NodeConfig.Config.MLTMINTF, ConfigConstants.DEFAULT_CONFIG_MLTMINTF);
        
        configStrMap.put(NodeConfig.Config.FAILEDLIMIT, ConfigConstants.FAILEDLIMIT);
        configStrMap.put(NodeConfig.Config.TIKATIMEOUT, ConfigConstants.TIKATIMEOUT);
        configStrMap.put(NodeConfig.Config.OTHERTIMEOUT, ConfigConstants.OTHERTIMEOUT);
        configStrMap.put(NodeConfig.Config.INDEXLIMIT, ConfigConstants.INDEXLIMIT);
        configStrMap.put(NodeConfig.Config.REINDEXLIMIT, ConfigConstants.REINDEXLIMIT);
    
        configStrMap.put(NodeConfig.Config.MLTCOUNT, ConfigConstants.MLTCOUNT);
        configStrMap.put(NodeConfig.Config.MLTMINDF, ConfigConstants.MLTMINDF);
        configStrMap.put(NodeConfig.Config.MLTMINTF, ConfigConstants.MLTMINTF);
    }
    
}
