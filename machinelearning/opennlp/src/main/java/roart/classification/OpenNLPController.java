package roart.classification;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.config.NodeConfig;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class OpenNLPController extends MachineLearningAbstractController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(OpenNLPController.class, args);
	}

	@Override
	protected MachineLearningAbstractClassifier createClassifier(String nodename, NodeConfig nodeConf) {
		return new OpennlpClassify(nodename, nodeConf);
	}
}
