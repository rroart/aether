package roart.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import roart.common.config.ConfigConstants;
import roart.common.constants.FileSystemConstants.FileSystemType;
import roart.common.util.JarThread;
import roart.common.util.XmlFs;

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
        Runnable eureka = new JarThread("aether-eureka-0.10-SNAPSHOT.jar", null);
        new Thread(eureka).start();
        Runnable core = new JarThread("aether-core-0.10-SNAPSHOT.jar", args);
        new Thread(core).start();
        String configFile = null;
        if (args.length > 1) {
            configFile = args[0];
        }
        String myConfigFile = configFile;
        if (myConfigFile == null) {
            myConfigFile = "../conf/" + ConfigConstants.CONFIGFILE;
        }
        Set<FileSystemType> fileSystems = new HashSet<>();
        fileSystems.add(FileSystemType.LOCAL);
        startFsService(myConfigFile, fileSystems);
    }
    
    public static void startFsService(String configFile, Set<FileSystemType> fileSystems) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        Map<FileSystemType, List<String>> filesystemMap = new XmlFs().getDirListMap(new File(configFile));
        startFsService(filesystemMap, fileSystems);
    } 
        
    public static void startFsService(Map<FileSystemType, List<String>> filesystemMap, Set<FileSystemType> fileSystems) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        for (Entry<FileSystemType, List<String>> entry : filesystemMap.entrySet()) {
            FileSystemType myType = entry.getKey();
            if (!fileSystems.contains(myType)) {
                continue;
            }
            //  -DFS=hdfs -DPATH=/tmp -DZOO=localhost:2181.
            List<String> paths = entry.getValue();
            String path = StringUtils.join(paths, ',');
            /*
            int index = entry.indexOf(':');
            if (index >= 0) {
                path = path.substring(index + 1);
            }
            */
            String[] myargs = new String[3];
            myargs[0] = "-DFS=" + myType;
            myargs[1] = "-DPATH" + path;
            myargs[2] = "-DZOO=localhost:2181";
            Runnable local = new JarThread("aether-" + myType + "-0.10-SNAPSHOT.jar", myargs);
            new Thread(local).start();
        }

    }

    public static void startFsServiceWithDirList(String dirlist, Set<FileSystemType> fileSystems) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        Map<FileSystemType, List<String>> filesystemMap = new XmlFs().getDirListMap(dirlist);
        startFsService(filesystemMap, fileSystems);        
    }
}
