package roart.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.constants.FileSystemConstants;
import roart.common.config.ConfigConstants;
import roart.common.config.Converter;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.FileSystemConstants.FileSystemType;
import roart.controller.SimpleController;
import roart.common.util.JarThread;
import roart.common.util.JsonUtil;
import roart.common.util.XmlFs;

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

    public void config() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        String version = "-0.10-SNAPSHOT.jar";
        Map<String, String> map2 = new HashMap<>();
        map2.put(EurekaConstants.CALIBRE, "aether-calibre" + version);
        map2.put(EurekaConstants.DJVUTXT, "aether-djvutxt" + version);
        map2.put(EurekaConstants.PDFTOTEXT, "aether-pdftotext" + version);
        map2.put(EurekaConstants.TIKA, "aether-tika" + version);
        map2.put(EurekaConstants.WVTEXT, "aether-wvtext" + version);
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
        map.put(ConfigConstants.FILESYSTEMS3, "aether-s3" + version);

        Map<String, String> fsmap = new HashMap<>();
        //fsmap.put(ConfigConstants.FILESYSTEMLOCAL, FileSystemType.HDFS);
        fsmap.put(ConfigConstants.FILESYSTEMHDFS, FileSystemConstants.HDFSTYPE);
        fsmap.put(ConfigConstants.FILESYSTEMSWIFT, FileSystemConstants.SWIFTTYPE);
        fsmap.put(ConfigConstants.FILESYSTEMS3, FileSystemConstants.S3TYPE);

        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            Boolean bool = (Boolean) configInstance.getValueOrDefault(key);
            if (bool == null) {
                continue;
            }
            if (bool) {
                String jar = entry.getValue();
                log.info("Starting {}", jar);
                switch (entry.getKey()) {
                // make this oo again
                case ConfigConstants.FILESYSTEMHDFS:
                case ConfigConstants.FILESYSTEMSWIFT:
                case ConfigConstants.FILESYSTEMS3:
                    Set<String> fileSystems = new HashSet<>();
                    fileSystems.add(fsmap.get(entry.getKey()));
                    String dirlist = (String) configInstance.getValueOrDefault(ConfigConstants.FSDIRLIST);
                    SimpleController.startFsServiceWithDirList(dirlist, fileSystems);
                    break;
                case ConfigConstants.MACHINELEARNINGMAHOUTSPARK:
                    Runnable def5 = new JarThread(jar, new String[] { "--add-opens", "java.base/java.util=ALL-UNNAMED", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "--add-opens", "java.base/java.util.concurrent=ALL-UNNAMED", "--add-opens", "java.base/java.nio=ALL-UNNAMED", "--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED" });
                    new Thread(def5).start();
                    break;
                case ConfigConstants.MACHINELEARNINGSPARKML:
                    Runnable def4 = new JarThread(jar, new String[] { "--add-opens", "java.base/java.nio=ALL-UNNAMED", "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens", "java.base/java.util=ALL-UNNAMED", "--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED" });
                    new Thread(def4).start();
                    break;
                case ConfigConstants.DATABASEHBASE:
                    Runnable def2 = new JarThread(jar, new String[] { "--add-opens", "java.base/java.nio=ALL-UNNAMED" });
                    new Thread(def2).start();
                    break;
                case ConfigConstants.DATABASEDYNAMODB:
                    Runnable def3 = new JarThread(jar, new String[] { "--add-opens", "java.base/java.lang=ALL-UNNAMED" });
                    new Thread(def3).start();
                    break;
                default:
                    Runnable def = new JarThread(jar, null);
                    new Thread(def).start();
                }
            }
        }
        String converterString = configInstance.getConverters();
        Converter[] converters = JsonUtil.convert(converterString, Converter[].class);
        log.info("convs"+converters.length);
        for (int i = 0; i < converters.length; i++) {
            Converter converter = converters[i];
            String name = converter.getName();
            String jar = map2.get(name.toUpperCase());
            log.info("convs"+name);
            if (jar != null) {
                log.info("convs"+name);
                Runnable def = new JarThread(jar, null);
                new Thread(def).start();                
            }
        }

    }
}
