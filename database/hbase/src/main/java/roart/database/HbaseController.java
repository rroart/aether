package roart.database;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.config.NodeConfig;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class HbaseController extends DatabaseAbstractController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(HbaseController.class, args);
	}

	@Override
	protected DatabaseOperations createOperations(String nodename, NodeConfig nodeConf) {
		return new HbaseIndexFilesWrapper(nodename, nodeConf);
	}
}
