package roart.database.cassandra;

import roart.common.config.NodeConfig;
import roart.database.DatabaseAbstractController;
import roart.database.DatabaseOperations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class CassandraController extends DatabaseAbstractController {

	public static void main(String[] args) {
	    System.out.println("main");
		SpringApplication.run(CassandraController.class, args);
	}

	@Override
	protected DatabaseOperations createOperations(String nodename, NodeConfig nodeConf) {
		return new CassandraIndexFilesWrapper(nodename, nodeConf);
	}
}
