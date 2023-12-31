package roart.filesystem.s3;

import roart.common.config.NodeConfig;
import roart.common.constants.FileSystemConstants;
import roart.common.constants.QueueConstants;
import roart.filesystem.FileSystemAbstractController;
import roart.filesystem.FileSystemOperations;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class S3Controller extends FileSystemAbstractController {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(S3Controller.class, args);
    }

    @Override
    protected FileSystemOperations createOperations(String configname, String configid, NodeConfig nodeConf, CuratorFramework curatorClient) {
        return new S3(configname, configid, nodeConf, curatorClient);
    }

    @Override
    protected String getFs() {
        return FileSystemConstants.S3TYPE;
    }

    @Override
    public String getQueueName() {
        return QueueConstants.S3;
    }
}
