package roart.database.hbase;

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
public class HbaseController extends DatabaseAbstractController {

	public static void main(String[] args) {
		SpringApplication.run(HbaseController.class, args);
	}

	@Override
	protected DatabaseOperations createOperations(String configname, String configid, NodeConfig nodeConf) {
		return new HbaseIndexFilesWrapper(configname, configid, nodeConf);
	}
}
