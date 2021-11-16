package roart.convert.impl;

import roart.common.config.NodeConfig;
import roart.convert.ConvertAbstract;
import roart.convert.ConvertAbstractController;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class DjvutxtController extends ConvertAbstractController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DjvutxtController.class, args);
	}

    @Override
    protected ConvertAbstract createConvert(String nodename, NodeConfig nodeConf) {
        return new Djvutxt(nodename, nodeConf);
    }
}
