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
public class CalibreController extends ConvertAbstractController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CalibreController.class, args);
	}

    @Override
    protected ConvertAbstract createConvert(String configname, String configid, NodeConfig nodeConf) {
        return new Calibre(configname, configid, nodeConf);
    }
}

