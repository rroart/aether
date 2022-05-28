package roart.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.FileSystemConstants;
import roart.common.constants.FileSystemConstants.FileSystemType;
import roart.common.model.FileObject;
import roart.common.model.Location;
import roart.common.util.JarThread;
import roart.common.util.XmlFs;
import roart.config.MyXMLConfig;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class SimpleController implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SimpleController.class, args);
    }

    @Override
    public void run(String... args) throws InterruptedException, XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        //MyXMLConfig instance = MyXMLConfig.instance();
        //instance.config();
        //Runnable eureka = new JarThread("aether-eureka-0.10-SNAPSHOT.jar", null);
        //new Thread(eureka).start();
        String configFile = null;
        if (args.length > 0) {
            configFile = args[0];
        }
        String myConfigFile = configFile;
        if (myConfigFile == null) {
            myConfigFile = "../conf/" + ConfigConstants.CONFIGFILE;
        }
        Runnable core = new JarThread("aether-core-0.10-SNAPSHOT.jar", null, new String[] { myConfigFile });
        //new Thread(core).start();
        //Set<String> fileSystems = new HashSet<>();
        //fileSystems.add(FileSystemConstants.LOCALTYPE);
        //startFsService(myConfigFile, fileSystems);
    }
    
    /*
    public static void startFsService(String configFile, Set<String> fileSystems) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        Map<Location, List<FileObject>> filesystemMap = new XmlFs().getDirListMap(new File(configFile));
        startFsService(filesystemMap, fileSystems);
    } 
      */
    
    public static void startFsService(Map<Location, List<FileObject>> filesystemMap, Set<String> fileSystems, NodeConfig configInstance) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        for (Entry<Location, List<FileObject>> entry : filesystemMap.entrySet()) {
            Location loc = entry.getKey();
            if (loc.fs == null) {
                loc.fs = FileSystemConstants.LOCALTYPE;
            }
            if (!fileSystems.contains(loc.fs)) {
                continue;
            }
            //  -DFS=hdfs -DPATH=/tmp -DZOO=localhost:2181.
            List<FileObject> fos = entry.getValue();
            List<String> paths = fos.stream().map(e -> e.toString()).collect(Collectors.toList());
            String path = StringUtils.join(paths, ',');
            /*
            int index = entry.indexOf(':');
            if (index >= 0) {
                path = path.substring(index + 1);
            }
            */
            String zookeeperConnectionString = configInstance.getZookeeper();
            String[] myargs = new String[5];
            myargs[0] = "-DFS=" + loc.fs;
            myargs[1] = "-DPATH=" + path;
            myargs[2] = "-DZOO=" + zookeeperConnectionString;
            myargs[3] = "-DNODE=" + (loc.nodename != null ? loc.nodename : "");
            myargs[4] = "-DIP=" +  (System.getenv(Constants.LOCALIP) != null ? System.getenv(Constants.LOCALIP) : "");
            String myTypeStr = ("" + loc.fs).toLowerCase();
            Runnable local = new JarThread("aether-" + myTypeStr + "-0.10-SNAPSHOT.jar", myargs, "en_US.ISO8859-1");
            new Thread(local).start();
        }

    }

    public static void startFsServiceWithDirList(String dirlist, Set<String> fileSystems, NodeConfig configInstance) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        Map<Location, List<FileObject>> filesystemMap = new XmlFs().getDirListMap(dirlist);
        startFsService(filesystemMap, fileSystems, configInstance);        
    }
}
