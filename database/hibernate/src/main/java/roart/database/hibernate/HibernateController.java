package roart.database.hibernate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.common.config.NodeConfig;
import roart.common.constants.QueueConstants;
import roart.database.DatabaseAbstractController;
import roart.database.DatabaseOperations;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class HibernateController extends DatabaseAbstractController {

    public static void main(String[] args) {
        SpringApplication.run(HibernateController.class, args);
    }

    @Override
    protected DatabaseOperations createOperations(String configname, String configid, NodeConfig nodeConf) {
        return new HibernateIndexFilesWrapper(configname, configid, nodeConf);
    }
    
    @Override
    public String getQueueName() {
        return QueueConstants.HIBERNATE;
    }

    @Override
    public boolean useAppId() {
        return true;
    }
}
