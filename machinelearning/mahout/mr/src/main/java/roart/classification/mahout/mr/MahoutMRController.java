package roart.classification.mahout.mr;

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
public class MahoutMRController extends MachineLearningAbstractController {

	@Override
	protected MachineLearningAbstractClassifier createClassifier(String nodename, NodeConfig nodeConf) {
		return new MahoutClassify(nodename, nodeConf);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MahoutMRController.class, args);
	}

}
