package roart.search.lucene;

import roart.common.config.NodeConfig;
import roart.search.SearchEngineAbstractController;
import roart.search.SearchEngineAbstractSearcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class LuceneController extends SearchEngineAbstractController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(LuceneController.class, args);
	}

	@Override
	protected SearchEngineAbstractSearcher createSearcher(String nodename, NodeConfig nodeConf) {
		return new SearchLucene(nodename, nodeConf);
	}
}
