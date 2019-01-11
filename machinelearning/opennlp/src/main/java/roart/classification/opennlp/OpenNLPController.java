package roart.classification.opennlp;

import roart.classification.MachineLearningAbstractClassifier;
import roart.classification.MachineLearningAbstractController;
import roart.common.config.NodeConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;

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
