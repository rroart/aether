package roart.filesystem.swift;

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
public class SwiftController extends FileSystemAbstractController {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SwiftController.class, args);
    }

    @Override
    protected FileSystemOperations createOperations(String configname, String configid, NodeConfig nodeConf, CuratorFramework curatorClient) {
        return new Swift(configname, configid, nodeConf, curatorClient);
    }

    @Override
    protected String getFs() {
        return FileSystemConstants.SWIFTTYPE;
    }

    @Override
    public String getQueueName() {
        return QueueConstants.SWIFT;
    }
}
