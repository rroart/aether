package roart.controller;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WebApplication implements CommandLineRunner {

        public static void main(String[] args) throws Exception {
                SpringApplication.run(WebApplication.class, args);
        }

	@Override
	public void run(String... args) throws InterruptedException {
        EurekaUtil.initEurekaClient();
	}

}

