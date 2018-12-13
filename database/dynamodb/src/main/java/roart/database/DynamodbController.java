package roart.database;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.config.NodeConfig;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class DynamodbController extends DatabaseAbstractController {

	public static void main(String[] args) {
		SpringApplication.run(DynamodbController.class, args);
	}

	@Override
	protected DatabaseOperations createOperations(String nodename, NodeConfig nodeConf) {
		return new DynamodbIndexFilesWrapper(nodename, nodeConf);
	}
}
