package roart.filesystem;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.config.NodeConfig;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class HDFSController extends FileSystemAbstractController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(HDFSController.class, args);
	}

	@Override
	protected FileSystemOperations createOperations(String nodename, NodeConfig nodeConf) {
		return new HDFS(nodename, nodeConf);
	}
}
