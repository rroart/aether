package roart.filesystem.hdfs;

import roart.common.config.NodeConfig;
import roart.common.constants.FileSystemConstants;
import roart.common.constants.QueueConstants;
import roart.filesystem.FileSystemAbstractController;
import roart.filesystem.FileSystemOperations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class HDFSController extends FileSystemAbstractController {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(HDFSController.class, args);
    }

    @Override
    protected FileSystemOperations createOperations(String configname, String configid, NodeConfig nodeConf) {
        return new HDFS(configname, configid, nodeConf);
    }

    @Override
    protected String getFs() {
        return FileSystemConstants.HDFSTYPE;
    }

    @Override
    public String getQueueName() {
        return QueueConstants.HDFS;
    }
}
