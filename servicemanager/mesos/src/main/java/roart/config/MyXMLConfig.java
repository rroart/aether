package roart.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import roart.common.constants.Constants;
import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.util.MesosUtil;

public class MyXMLConfig {

    protected static Logger log = LoggerFactory.getLogger(MyConfig.class);
    
    protected static MyXMLConfig instance = null;
    
    public static MyXMLConfig instance(NodeConfig config) {
        if (instance == null) {
            configInstance = config;
            instance = new MyXMLConfig();
        }
        return instance;
    }

    protected static NodeConfig configInstance = null;
    
    public MyXMLConfig() {
        try {
            config();
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e); 
        }
    }

    public NodeConfig mynode() {
        return null;
    }

     public void config() throws JsonProcessingException {
         String version;
         version = "";
         Map<String, String> map = new HashMap<>();
         map.put(ConfigConstants.DATABASEHBASE, "aether-hbase" + version);
         map.put(ConfigConstants.DATABASECASSANDRA, "aether-cassandra" + version);
         map.put(ConfigConstants.DATABASEDYNAMODB, "aether-dynamodb" + version);
         map.put(ConfigConstants.DATABASEHIBERNATE, "aether-hibernate" + version);
         map.put(ConfigConstants.DATABASEDATANUCLEUS, "aether-datanucleus" + version);
         map.put(ConfigConstants.MACHINELEARNINGMAHOUT, "aether-mahout-mr" + version);
         map.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARK, "aether-mahout-spark" + version);
         map.put(ConfigConstants.MACHINELEARNINGSPARKML, "aether-spark-ml" + version);
         map.put(ConfigConstants.MACHINELEARNINGOPENNLP, "aether-opennlp" + version);
         map.put(ConfigConstants.SEARCHENGINESOLR, "aether-solr" + version);
         map.put(ConfigConstants.SEARCHENGINELUCENE, "aether-lucene" + version);
         map.put(ConfigConstants.SEARCHENGINEELASTIC, "aether-elastic" + version);
         map.put(ConfigConstants.FILESYSTEMLOCAL, "aether-local" + version);
         map.put(ConfigConstants.FILESYSTEMHDFS, "aether-hdfs" + version);
         map.put(ConfigConstants.FILESYSTEMSWIFT, "aether-swift" + version);
         map.put(ConfigConstants.FILESYSTEMS3, "aether-s3" + version);
         String eurekaURI = System.getenv("EUREKA_SERVER_URI");
         for (Entry<String, String> entry : map.entrySet()) {
             String key = entry.getKey();
             Boolean bool = (Boolean) configInstance.getValueOrDefault(key);
             if (bool) {
                 String jar = entry.getValue();
                 log.info("Starting {}", jar);
                 MesosUtil local = new MesosUtil();
                 local.start(jar, eurekaURI);
             }
         }
     }
}
