package roart.search.solr;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.common.config.NodeConfig;
import roart.search.SearchEngineAbstractController;
import roart.search.SearchEngineAbstractSearcher;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class SolrController extends SearchEngineAbstractController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SolrController.class, args);
	}

	@Override
	protected SearchEngineAbstractSearcher createSearcher(String nodename, NodeConfig nodeConf) {
		return new SearchSolr(nodename, nodeConf);
	}
}
