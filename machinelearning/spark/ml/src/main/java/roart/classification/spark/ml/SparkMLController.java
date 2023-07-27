package roart.classification.spark.ml;

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
public class SparkMLController extends MachineLearningAbstractController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SparkMLController.class, args);
	}

	@Override
	protected MachineLearningAbstractClassifier createClassifier(String configname, String configid, NodeConfig nodeConf) {
		return new SparkMLClassify(configname, configid, nodeConf);
	}
}
