package roart.convert;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.util.InmemoryUtil;
import roart.common.model.ConfigParam;
import roart.common.util.IOUtil;
import roart.common.util.JsonUtil;
import roart.common.zk.thread.ConfigThread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
//@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
public abstract class ConvertAbstractController implements CommandLineRunner {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    private static Map<String, ConvertAbstract> convertMap = new HashMap();

    private Map<String, ConvertQueue> queueMap = new HashMap<>();

    private CuratorFramework curatorClient;

    protected abstract ConvertAbstract createConvert(String configname, String configid, NodeConfig nodeConf, CuratorFramework curatorClient);

    ConvertAbstract getConvert(ConvertParam param) {
        ConvertAbstract convert = convertMap.get(param.configid);
        if (convert == null) {
            NodeConfig nodeConf = null;
            if (param.conf != null) {
                nodeConf = param.conf;
            }
            convert = createConvert(param.configname, param.configid, nodeConf, curatorClient);
            convertMap.put(param.configid, convert);
        }
        return convert;
    }

    private ConvertAbstract getConvert(ConfigParam param) {
        ConvertAbstract operation = convertMap.get(param.getConfigid());
        if (operation == null) {
            NodeConfig nodeConf = getNodeConf(param);
            operation = createConvert(param.getConfigname(), param.getConfigid(), nodeConf, curatorClient);
            convertMap.put(param.getConfigid(), operation);
            if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
                ConvertQueue queue = new ConvertQueue(getQueueName(), this, curatorClient, nodeConf);
                queueMap.put(param.getConfigid(), queue);
            }
            log.info("Created config for {} {}", param.getConfigname(), param.getConfigid());
        }
        return operation;
    }

    @RequestMapping(value = "/" + EurekaConstants.CONSTRUCTOR,
            method = RequestMethod.POST)
    public ConvertResult processConstructor(@RequestBody ConfigParam param)
            throws Exception {
        String error = null;
        try {
            ConvertAbstract operation = getConvert(param);
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
            error = e.getMessage();
        }
        ConvertResult result = new ConvertResult();
        result.error = error;
        return result;
    }
    
    @RequestMapping(value = "/" + EurekaConstants.CONVERT,
            method = RequestMethod.POST)
    public ConvertResult processSearch(@RequestBody ConvertParam param)
            throws Exception {
        ConvertAbstract convert = getConvert(param);
        ConvertResult ret = convert.convert(param);
        return ret;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConvertAbstractController.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);     

        boolean useHostName = Constants.TRUE.equals(System.getenv(Constants.USEHOSTNAME));

        String zookeeperConnectionString = System.getProperty("ZOO");
        if (zookeeperConnectionString == null) {
            zookeeperConnectionString = System.getenv("ZOO");
        }
        if (zookeeperConnectionString == null) {
            zookeeperConnectionString = "localhost:2181";
        }
        curatorClient = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        curatorClient.start();
        int port = webServerAppCtxt.getWebServer().getPort();
        new ConfigThread(zookeeperConnectionString, port, useHostName).run();
    }

    public abstract String getQueueName();

    private NodeConfig getNodeConf(ConfigParam param) {
        NodeConfig nodeConf = null;
        Inmemory inmemory = InmemoryFactory.get(param.getIserver(), param.getIconnection(), param.getIconnection());
        try (InputStream contentStream = inmemory.getInputStream(param.getIconf())) {
            if (InmemoryUtil.validate(param.getIconf().getMd5(), contentStream)) {
                String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArray1G(inmemory.getInputStream(param.getIconf())));
                nodeConf = JsonUtil.convertnostrip(content, NodeConfig.class);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return nodeConf;
    }

}
