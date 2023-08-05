package roart.classification.mahout.mr;

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
public class MahoutMRController extends MachineLearningAbstractController {

    @Override
    protected MachineLearningAbstractClassifier createClassifier(String configname, String configid, NodeConfig nodeConf) {
        return new MahoutClassify(configname, configid, nodeConf);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MahoutMRController.class, args);
    }


    @Override
    public String getQueueName() {
        return QueueConstants.MAHOUTMR;
    }
}
