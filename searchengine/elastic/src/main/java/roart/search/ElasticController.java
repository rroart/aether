package roart.search;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.config.NodeConfig;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class ElasticController extends SearchEngineAbstractController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ElasticController.class, args);
	}

	@Override
	protected SearchEngineAbstractSearcher createSearcher(String nodename, NodeConfig nodeConf) {
		return new SearchElastic(nodename, nodeConf);
	}
}
