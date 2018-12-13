package roart.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.util.Constants;
import roart.util.OpenshiftUtil;

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

     public void config() throws IOException, InterruptedException {
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
         map.put(ConfigConstants.FILESYSTEMHDFS, "aether-hdfs" + version);
         map.put(ConfigConstants.FILESYSTEMSWIFT, "aether-swift" + version);
         String openshift = System.getenv("OPENSHIFT_SERVER");
        String eureka = System.getenv("EUREKA_SERVER_URI");
         String repo = System.getenv("DOCKER_REPO");
         String dockerCertPath = System.getenv("DOCKER_CERT_PATH");
         String namespace = "myproject";
         for (Entry<String, String> entry : map.entrySet()) {
             String key = entry.getKey();
             Boolean bool = (Boolean) configInstance.getValueOrDefault(key);
             if (bool) {
                 String imageName = entry.getValue();
                 log.info("Starting {}", imageName);
                 OpenshiftUtil local = new OpenshiftUtil();
                 local.start(imageName, imageName, eureka, repo, namespace, openshift, dockerCertPath);
             }
         }
     }
}
