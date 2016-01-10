package roart.config;

import java.util.HashMap;
import java.util.Map;

public class NodeConfig {
    public String languages = null;
    
    public String dirlist[] = null;
    public String dirlistnot[] = null;
    
    public boolean hasHibernate = false;
    public String db = null;
    public String hbasequorum = null;
    public String hbaseport = null;
    public String hbasemaster = null;

    public String index = null;
    public String lucenepath = null;
    public String solrurl = null;
    public boolean highlightmlt = false;
    //public boolean searchsimilar = false;
    
    public Map<MyConfig.Config, Integer> configMap = new HashMap<MyConfig.Config, Integer>();
    public String fsdefaultname = null;
    public String zookeeper = null;
    public boolean zookeepersmall = false;
    public boolean distributedtraverse = false;
    public String locker = null;
    
    public String classify = null;
    public String opennlpmodelpath = null;
    
    public String mahoutconffs = null;
    public String mahoutbasepath = null;
    public String mahoutmodelpath = null;
    public String mahoutlabelindexpath = null;
    public String mahoutdictionarypath = null;
    public String mahoutdocumentfrequencypath = null;
    public String mahoutalgorithm = null;

    public boolean downloader = false;
    public boolean authenticate = false;
}
