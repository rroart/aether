package roart.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.util.Constants;
import roart.util.OpenshiftThread;
import roart.util.MyMap;

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
        // TODO fix
        return null; //getNode(ControlService.nodename);
    }

     public void config() {
         String version = "-0.10-SNAPSHOT.jar";
         version = "";
         Map<String, String> map = new HashMap<>();
         map.put(ConfigConstants.DATABASEHBASE, "aether-hbase" + version);
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
         //map.put(ConfigConstants., "");
         //map.put(ConfigConstants., "");
         String addr = System.getenv("EUREKA_SERVER_URI");
         String repo = "172.30.1.1:5000/myproject/";
         for (String key : map.keySet()) {
             Boolean bool = (Boolean) configInstance.getValueOrDefault(key);
             if (bool) {
                 String jar = map.get(key);
                 log.info("Starting " + jar);
                 OpenshiftThread local = new OpenshiftThread();
                 local.start(key, jar, addr, repo);
                          //Runnable local = new DockerThread(jar);
                 //new Thread(local).start();
             }
         }
     }
}
