package roart.filesystem;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.constants.FileSystemConstants;
import roart.common.filesystem.FileSystemBooleanResult;
import roart.common.filesystem.FileSystemByteResult;
import roart.common.filesystem.FileSystemConstructorParam;
import roart.common.filesystem.FileSystemConstructorResult;
import roart.common.filesystem.FileSystemFileObjectParam;
import roart.common.filesystem.FileSystemFileObjectResult;
import roart.common.filesystem.FileSystemMessageResult;
import roart.common.filesystem.FileSystemMyFileResult;
import roart.common.filesystem.FileSystemParam;
import roart.common.filesystem.FileSystemPathParam;
import roart.common.filesystem.FileSystemPathResult;
import roart.common.filesystem.FileSystemStringResult;
import roart.common.model.ConfigParam;
import roart.common.model.FileObject;
import roart.common.util.FsUtil;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.util.InmemoryUtil;
import roart.common.zk.thread.ConfigThread;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
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
import roart.common.util.IOUtil;
import roart.common.util.JsonUtil;

@RestController
//@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
public abstract class FileSystemAbstractController implements CommandLineRunner {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    private static Map<String, FileSystemOperations> operationMap = new HashMap();

    private static Map<String, FileSystemQueue> queueMap = new HashMap();

    private CuratorFramework curatorClient;

    protected abstract FileSystemOperations createOperations(String configname, String configid, NodeConfig nodeConf, CuratorFramework curatorClient);

    FileSystemOperations getOperations(FileSystemParam param) {
        FileSystemOperations operation = operationMap.get(param.configid);
        if (operation == null) {
            NodeConfig nodeConf = null;
            if (param.conf != null) {
                nodeConf = param.conf;
            }
            operation = createOperations(param.configname, param.configid, nodeConf, curatorClient);
            operationMap.put(param.configid, operation);
        }
        return operation;
    }

    private FileSystemOperations getOperations(ConfigParam param) {
        FileSystemOperations operation = operationMap.get(param.getConfigid());
        if (operation == null) {
            NodeConfig nodeConf = getNodeConf(param);
            operation = createOperations(param.getConfigname(), param.getConfigid(), nodeConf, curatorClient);
            operationMap.put(param.getConfigid(), operation);
            String appid = System.getenv(Constants.APPID) != null ? System.getenv(Constants.APPID) : "";
            if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
                FileSystemQueue queue = new FileSystemQueue(getQueueName() + appid, this, curatorClient, nodeConf);
                queueMap.put(param.getConfigid(),  queue);
            }
            log.info("Created config for {} {}", param.getConfigname(), param.getConfigid());
        }
        return operation;
    }

    protected abstract String getFs();
    
    @RequestMapping(value = "/" + EurekaConstants.CONSTRUCTOR,
            method = RequestMethod.POST)
    public FileSystemConstructorResult processConstructor(@RequestBody ConfigParam param)
            throws Exception {
        String error = null;
        try {
            FileSystemOperations operations = getOperations(param);
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
            error = e.getMessage();
        }
        FileSystemConstructorResult result = new FileSystemConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DESTRUCTOR,
            method = RequestMethod.POST)
    public FileSystemConstructorResult processDestructor(@RequestBody FileSystemConstructorParam param)
            throws Exception {
        FileSystemOperations operations = operationMap.remove(param.configid);
        String error = null;
        if (operations != null) {
            try {
                operations.destroy();
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
                error = e.getMessage();
            }
        } else {
            error = "did not exist";
        }
        FileSystemConstructorResult result = new FileSystemConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.LISTFILES,
            method = RequestMethod.POST)
    public FileSystemFileObjectResult processListFiles(@RequestBody FileSystemFileObjectParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemFileObjectResult ret = operations.listFiles(param);
        return ret;
    }

    @PostMapping(value = "/" + EurekaConstants.LISTFILESFULL)
    public FileSystemMyFileResult processListFilesFull(@RequestBody FileSystemFileObjectParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemMyFileResult ret = operations.listFilesFull(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.EXIST,
            method = RequestMethod.POST)
    public FileSystemBooleanResult processExist(@RequestBody FileSystemFileObjectParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemBooleanResult ret = operations.exists(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.GETABSOLUTEPATH,
            method = RequestMethod.POST)
    public FileSystemPathResult processGetAbsolutePath(@RequestBody FileSystemFileObjectParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemPathResult ret = operations.getAbsolutePath(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.ISDIRECTORY,
            method = RequestMethod.POST)
    public FileSystemBooleanResult processIsDirectory(@RequestBody FileSystemFileObjectParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemBooleanResult ret = operations.isDirectory(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.GETINPUTSTREAM,
            method = RequestMethod.POST)
    public FileSystemByteResult processGetInputStream(@RequestBody FileSystemFileObjectParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemByteResult ret = operations.getInputStream(param);
        return ret;
    }

    @PostMapping(value = "/" + EurekaConstants.GETWITHINPUTSTREAM)
    public FileSystemMyFileResult processGetWithInputStream(@RequestBody FileSystemPathParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemMyFileResult ret = operations.getWithInputStream(param, true);
        return ret;
    }

    @PostMapping(value = "/" + EurekaConstants.GETWITHOUTINPUTSTREAM)
    public FileSystemMyFileResult processGetWithoutInputStream(@RequestBody FileSystemPathParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemMyFileResult ret = operations.getWithInputStream(param, false);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.GETPARENT,
            method = RequestMethod.POST)
    public FileSystemFileObjectResult processGetParent(@RequestBody FileSystemFileObjectParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemFileObjectResult ret = operations.getParent(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.GET,
            method = RequestMethod.POST)
    public FileSystemFileObjectResult processGet(@RequestBody FileSystemPathParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemFileObjectResult ret = operations.get(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.READFILE,
            method = RequestMethod.POST)
    public FileSystemMessageResult processReadFile(@RequestBody FileSystemFileObjectParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemMessageResult ret = operations.readFile(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.GETMD5,
            method = RequestMethod.POST)
    public FileSystemStringResult processGetMd5(@RequestBody FileSystemFileObjectParam param)
            throws Exception {
        FileSystemOperations operations = getOperations(param);
        FileSystemStringResult ret = operations.getMd5(param);
        return ret;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(FileSystemAbstractController.class, args);
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
        new Thread(new FileSystemThread(curatorClient, port, getFs())).start();
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
