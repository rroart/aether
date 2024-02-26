package roart.database;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.database.DatabaseConstructorParam;
import roart.common.database.DatabaseConstructorResult;
import roart.common.database.DatabaseFileLocationParam;
import roart.common.database.DatabaseIndexFilesParam;
import roart.common.database.DatabaseIndexFilesResult;
import roart.common.database.DatabaseLanguagesResult;
import roart.common.database.DatabaseMd5Param;
import roart.common.database.DatabaseMd5Result;
import roart.common.database.DatabaseParam;
import roart.common.database.DatabaseResult;
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
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public abstract class DatabaseAbstractController implements CommandLineRunner {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    private static Map<String, DatabaseOperations> operationMap = new HashMap<>();

    private Map<String, DatabaseQueue> queueMap = new HashMap<>();

    private CuratorFramework curatorClient;

    protected abstract DatabaseOperations createOperations(String configname, String configid, NodeConfig nodeConf);

    DatabaseOperations getOperation(DatabaseParam param) {
        DatabaseOperations operation = operationMap.get(param.getConfigid());
        log.info("Keys {}", operationMap.keySet());
        log.info("Get config for {} {}", param.getConfigname(), param.getConfigid());
        if (operation == null) {
            NodeConfig nodeConf = null;
            if (param.getConf() != null) {
                nodeConf = param.getConf();
            }
            operation = createOperations(param.getConfigname(), param.getConfigid(), nodeConf);
            operationMap.put(param.getConfigid(), operation);
        }
        return operation;
    }

    private DatabaseOperations getOperation(ConfigParam param) {
        DatabaseOperations operation = operationMap.get(param.getConfigid());
        if (operation == null) {
            NodeConfig nodeConf = getNodeConf(param);
            operation = createOperations(param.getConfigname(), param.getConfigid(), nodeConf);
            operationMap.put(param.getConfigid(), operation);
            String appid = useAppId() && System.getenv("APPID") != null ? System.getenv("APPID") : "";
            DatabaseQueue queue = new DatabaseQueue(getQueueName() + appid, this, curatorClient, nodeConf);
            queueMap.put(param.getConfigid(),  queue);
            log.info("Created config for {} {}", param.getConfigname(), param.getConfigid());
        }
        return operation;
    }

    @RequestMapping(value = "/" + EurekaConstants.CONSTRUCTOR,
            method = RequestMethod.POST)
    public DatabaseConstructorResult processConstructor(@RequestBody ConfigParam param)
            throws Exception {
        String error = null;
        try {
            DatabaseOperations operation = getOperation(param);
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
            error = e.getMessage();
        }
        DatabaseConstructorResult result = new DatabaseConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DESTRUCTOR,
            method = RequestMethod.POST)
    public DatabaseConstructorResult processDestructor(@RequestBody DatabaseConstructorParam param)
            throws Exception {
        DatabaseOperations operation = operationMap.remove(param.getConfigid());
        String error = null;
        if (operation != null) {
            try {
                operation.destroy();
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
                error = e.getMessage();
            }
        } else {
            error = "did not exist";
        }
        DatabaseConstructorResult result = new DatabaseConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.CLEAR,
            method = RequestMethod.POST)
    public DatabaseConstructorResult processClear(@RequestBody DatabaseConstructorParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.clear(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.DROP,
            method = RequestMethod.POST)
    public DatabaseConstructorResult processDrop(@RequestBody DatabaseConstructorParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.drop(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETFILELOCATIONSBYMD5,
            method = RequestMethod.POST)
    public DatabaseResult processGetFilelocationsByMd5(@RequestBody DatabaseMd5Param param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.getFilelocationsByMd5(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETBYFILELOCATION,
            method = RequestMethod.POST)
    public DatabaseIndexFilesResult processGetByFilelocation(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.getByFilelocation(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETBYMD5,
            method = RequestMethod.POST)
    public DatabaseIndexFilesResult processGetByMd5(@RequestBody DatabaseMd5Param param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.getByMd5(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETMD5BYFILELOCATION,
            method = RequestMethod.POST)
    public DatabaseMd5Result processGetMd5ByFilelocation(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.getMd5ByFilelocation(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETALL,
            method = RequestMethod.POST)
    public DatabaseIndexFilesResult processGetAll(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.getAll(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETALLFILES,
            method = RequestMethod.POST)
    public DatabaseIndexFilesResult processGetAllFiles(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.getAllFiles(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.SAVE,
            method = RequestMethod.POST)
    public DatabaseResult processSave(@RequestBody DatabaseIndexFilesParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.save(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.FLUSH,
            method = RequestMethod.POST)
    public DatabaseResult processFlush(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.flush(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.COMMIT,
            method = RequestMethod.POST)
    public DatabaseResult processCommit(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.commit(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.CLOSE,
            method = RequestMethod.POST)
    public DatabaseResult processClose(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.close(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETALLMD5,
            method = RequestMethod.POST)
    public DatabaseMd5Result processGetAllMd5(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.getAllMd5(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETLANGUAGES,
            method = RequestMethod.POST)
    public DatabaseLanguagesResult processGetLanguages(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.getLanguages(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.DELETE,
            method = RequestMethod.POST)
    public DatabaseResult processDelete(@RequestBody DatabaseIndexFilesParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param);
        return operation.delete(param);
    }

    public static void main(String[] args) {
        SpringApplication.run(DatabaseAbstractController.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);     

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
        new ConfigThread(zookeeperConnectionString, port, false).run();
    }

    public abstract String getQueueName();
    
    public boolean useAppId( ) {
        return false;
    };
    
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
