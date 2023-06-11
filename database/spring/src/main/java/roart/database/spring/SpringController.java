package roart.database.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.common.config.NodeConfig;
import roart.database.DatabaseAbstractController;
import roart.database.DatabaseOperations;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
@EnableJdbcRepositories("roart.database.spring")
@EnableAutoConfiguration
@ComponentScan(basePackages = "roart.database.spring")
public class SpringController extends DatabaseAbstractController {

    public static void main(String[] args) {
        SpringApplication.run(SpringController.class, args);
    }

    @Autowired
    private SpringIndexFilesWrapper springIndexFilesWrapper;
    
    @Override
    protected DatabaseOperations createOperations(String nodename, NodeConfig nodeConf) {
        return springIndexFilesWrapper;
    }
}
