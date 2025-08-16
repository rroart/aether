package roart.classification;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.web.bind.annotation.*;

import roart.common.machinelearning.MachineLearningConstructorParam;
import roart.common.machinelearning.MachineLearningConstructorResult;
import roart.common.machinelearning.MachineLearningParam;
import roart.common.model.ConfigParam;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.util.InmemoryUtil;
import roart.common.util.IOUtil;
import roart.common.util.JsonUtil;
import roart.common.zk.thread.ConfigThread;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.slf4j.LoggerFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
//@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
public abstract class MachineLearningAbstractController implements CommandLineRunner {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    private static Map<String, MachineLearningAbstractClassifier> classifierMap = new HashMap();

    private Map<String, MachineLearningQueue> queueMap = new HashMap<>();

    private CuratorFramework curatorClient;

    MachineLearningAbstractClassifier getClassifier(MachineLearningParam param) {
        MachineLearningAbstractClassifier classifier = classifierMap.get(param.configid);
        if (classifier == null) {
            NodeConfig nodeConf = null;
            if (param.conf != null) {
                nodeConf = param.conf;
            }
            classifier = createClassifier(param.configname, param.configid, nodeConf);
            classifierMap.put(param.configid, classifier);
        }
        return classifier;
    }

    private MachineLearningAbstractClassifier getClassifier(ConfigParam param) {
        MachineLearningAbstractClassifier operation = classifierMap.get(param.getConfigid());
        if (operation == null) {
            NodeConfig nodeConf = getNodeConf(param);
            operation = createClassifier(param.getConfigname(), param.getConfigid(), nodeConf);
            classifierMap.put(param.getConfigid(), operation);
            String appid = System.getenv(Constants.APPID) != null ? System.getenv(Constants.APPID) : "";
            if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
                MachineLearningQueue queue = new MachineLearningQueue(getQueueName() + appid, this, curatorClient, nodeConf);
                queueMap.put(param.getConfigid(),  queue);
            }
            log.info("Created config for {} {}", param.getConfigname(), param.getConfigid());
        }
        return operation;
    }

    protected abstract MachineLearningAbstractClassifier createClassifier(String configname, String configid, NodeConfig nodeConf);

    @RequestMapping(value = "/" + EurekaConstants.CONSTRUCTOR,
            method = RequestMethod.POST)
    public MachineLearningConstructorResult processConstructor(@RequestBody ConfigParam param)
            throws Exception {
        String error = null;
        try {
            MachineLearningAbstractClassifier classifier = getClassifier(param);
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
            error = e.getMessage();
        }
        MachineLearningConstructorResult result = new MachineLearningConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DESTRUCTOR,
            method = RequestMethod.POST)
    public MachineLearningConstructorResult processDestructor(@RequestBody MachineLearningConstructorParam param)
            throws Exception {
        MachineLearningAbstractClassifier classifier = classifierMap.remove(param.configid);
        String error = null;
        if (classifier != null) {
            try {
                classifier.destroy(param.configname);
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
                error = e.getMessage();
            }
        } else {
            error = "did not exist";
        }
        MachineLearningConstructorResult result = new MachineLearningConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.CLASSIFY,
            method = RequestMethod.POST)
    public MachineLearningClassifyResult processClassify(@RequestBody MachineLearningClassifyParam param)
            throws Exception {
        MachineLearningAbstractClassifier classifier = getClassifier(param);
        MachineLearningClassifyResult ret = classifier.classify(param);
        return ret;
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
        new ConfigThread(zookeeperConnectionString, port, useHostName, this::handleConfig).run();
    }

    public abstract String getQueueName();

    private Integer handleConfig(Queue<String> params) {
        for (String param : params) {
            ConfigParam configParam = JsonUtil.convertnostrip(param, ConfigParam.class);
            if (configParam == null) {
                log.error("Can not use {}", param);
                continue;
            }
            getClassifier(configParam);
        }
        return 0;
    }

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
