package roart.classification.mahout.spark;

import roart.classification.MachineLearningAbstractClassifier;
import roart.classification.MachineLearningAbstractController;
import roart.common.config.NodeConfig;
import roart.common.constants.QueueConstants;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class MahoutSparkController extends MachineLearningAbstractController {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MahoutSparkController.class, args);
    }

    @Override
    protected MachineLearningAbstractClassifier createClassifier(String configname, String configid, NodeConfig nodeConf) {
        return new MahoutSparkClassify(configname, configid, nodeConf);
    }

    @Override
    public String getQueueName() {
        return QueueConstants.MAHOUTSPARK;
    }
}
