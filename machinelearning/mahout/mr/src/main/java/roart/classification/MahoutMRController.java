package roart.classification;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.config.NodeConfig;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

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
