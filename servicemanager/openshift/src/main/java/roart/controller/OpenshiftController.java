package roart.controller;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.config.NodeConfig;
import roart.util.DockerRunUtil;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class OpenshiftController implements CommandLineRunner {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(OpenshiftController.class, args);
	}
	
    @Override
    public void run(String... args) throws InterruptedException {
        DockerRunUtil.run();
    }
}
