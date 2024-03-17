package roart.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocator.FileLocatorBuilder;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.io.FileUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyCollections;
import roart.common.collections.MyMap;
import roart.common.collections.impl.MyHazelcastRemover;
import roart.common.collections.impl.MyMaps;
import roart.common.config.ConfigConstants;
import roart.common.config.ConfigTreeMap;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.util.JsonUtil;
import roart.common.zkutil.ZKMessageUtil;
import roart.common.hcutil.GetHazelcastInstance;
import roart.lang.LanguageDetect;
import roart.service.ControlService;
import roart.thread.EurekaThread;

public class MyXMLConfig {

    protected static Logger log = LoggerFactory.getLogger(MyConfig.class);

    protected static MyXMLConfig instance = null;

    MyMap nodemap = null;

    public static MyXMLConfig instance(String configFile) {
        if (instance == null) {
            instance = new MyXMLConfig(configFile);
            if (configInstance == null) {
                getConfigInstance(configFile);
            }
        }
        return instance;
    }

    protected static NodeConfig configInstance = null;

    public static NodeConfig getConfigInstance(String configFile) {
        if (configInstance == null) {
            configInstance = new NodeConfig();
            MyConfig.conf = configInstance;
            MyConfig.time = System.currentTimeMillis();
            
            if (instance == null) {
                instance(configFile);
            }
        }
        return configInstance;
    }

    private static Configuration config = null;
    private static XMLConfiguration configxml = null;

    public MyXMLConfig(String myConfigFile) {
        try {
            //String myConfigFile = configFile;
            log.info("myconf " + myConfigFile);
            getConfigInstance(myConfigFile);
            configxml = new XMLConfiguration();
            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<XMLConfiguration> fileBuilder =
                    new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                    .configure(params.fileBased().setFileName(myConfigFile));
            InputStream stream = new FileInputStream(new File(myConfigFile));         
            configxml = fileBuilder.getConfiguration();
            configxml.read(stream);
            String root = configxml.getRootElementName();
            Document doc = configxml.getDocument();
            configInstance.configTreeMap = new ConfigTreeMap();
            configInstance.configValueMap = new HashMap<String, Object>();
            System.out.println("v0 " + configInstance.configValueMap + " " + 0);
            ConfigConstantMaps.makeDefaultMap();
            ConfigConstantMaps.makeTextMap();
            ConfigConstantMaps.makeTypeMap();
            configInstance.deflt = ConfigConstantMaps.deflt;
            configInstance.type = ConfigConstantMaps.map;
            configInstance.text = ConfigConstantMaps.text;
            handleDoc(doc.getDocumentElement(), configInstance.configTreeMap, "");

            Iterator<String> iter = configxml.getKeys();
            //System.out.println("keys " + ConfigConstants.map.keySet());
            while(iter.hasNext()) {
                String s = iter.next();
                //System.out.println("s " + s + " " + configxml.getString(s) + " " + configxml.getProperty(s));
                Object o = null;
                String text = s;
                Class myclass = ConfigConstantMaps.map.get(text);

                if (myclass == null) {
                    System.out.println("Unknown " + text);
                    log.info("Unknown " + text);
                    continue;
                }
                String str = configxml.getString(s);
                switch (myclass.getName()) {
                case "java.lang.String":
                    o = configxml.getString(s);
                    break;
                case "java.lang.Integer":
                    o = !str.isEmpty() ? configxml.getInt(s) : null;
                    break;
                case "java.lang.Double":
                    o = !str.isEmpty() ? configxml.getDouble(s) : null;
                    break;
                case "java.lang.Boolean":
                    o = !str.isEmpty() ? configxml.getBoolean(s) : null;
                    break;
                default:
                    System.out.println("unknown " + myclass.getName());
                    log.info("unknown " + myclass.getName());
                }
                configInstance.configValueMap.put(s, o);
            }
            //((AbstractConfiguration) config).setDelimiterParsingDisabled(true);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e); 
        }
        /*
        if (!"false".equals(System.getProperty("eureka.client.enabled"))) {
        Runnable confMe = new EurekaThread(configInstance);
        confMe.run();
        }
        */
        //new Thread(confMe).start();

    }

    private void print(ConfigTreeMap map2, int indent) {
        String space = "      ";
        System.out.print(space.substring(0, indent));
        System.out.println("map2 " + map2.name + " " + map2.enabled);
        Map<String, ConfigTreeMap> map3 = map2.configTreeMap;
        for (String key : map3.keySet()) {
            print(map3.get(key), indent + 1);
            //Object value = map.get(key);
            //System.out.println("k " + key + " " + value + " " + value.getClass().getName());
        }

    }

    private void handleDoc(Element documentElement, ConfigTreeMap configMap, String baseString) {
        String name = documentElement.getNodeName();
        String basename = name;
        String attribute = documentElement.getAttribute("enable");
        NodeList elements = documentElement.getChildNodes();
        boolean leafNode = elements.getLength() == 0;
        Boolean enabled = null;
        if (attribute != null) {
            enabled = !attribute.equals("false");
            if (/*leafNode &&*/ !attribute.isEmpty()) {
                name = name + "[@enable]";
            }
        }
        configMap.name = baseString + "." + name;
        configMap.name = configMap.name.replaceFirst(".config.", "");
        //System.out.println("name " + configMap.name);
        if (leafNode) {
            //enabled = null;
        }
        configMap.enabled = enabled;
        configMap.configTreeMap = new HashMap<String, ConfigTreeMap>();
        for (int i = 0; i < elements.getLength(); i++) {
            Node node = elements.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                ConfigTreeMap newMap = new ConfigTreeMap();
                Element element = (Element) node;
                String newBaseString = baseString + "." + basename;
                newBaseString = newBaseString.replaceFirst(".config.", "");
                handleDoc(element, newMap, newBaseString);
                String text = element.getNodeName();
                configMap.configTreeMap.put(text, newMap);
            }
        }


    }
    public static String[] fsvalues = { ConfigConstants.LOCAL, ConfigConstants.SWIFT, ConfigConstants.HADOOP };
    public static String[] lockmodevalues = { ConfigConstants.SMALL, ConfigConstants.BIG };

    public NodeConfig mynode() {
        // TODO fix
        return null; //getNode(ControlService.getConfigName());
    }

    public void myput(String nodename, NodeConfig config) {
        nodemap.put(nodename, config);
        ZKMessageUtil.doreconfig(null /*controlService.getConfigName()*/);
    }

}
