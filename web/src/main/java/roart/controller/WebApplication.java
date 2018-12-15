package roart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.netflix.discovery.DiscoveryClient;

import roart.util.EurekaUtil;

@SpringBootApplication
@EnableDiscoveryClient
public class WebApplication implements CommandLineRunner {

    @Autowired
    public static DiscoveryClient discoveryClient; // = null;

    public static void main(String[] args) throws Exception {
        System.out.println("haha " + discoveryClient);
        SpringApplication.run(WebApplication.class, args);
        System.out.println("haha1 " + discoveryClient);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        System.out.println("haha2 " + discoveryClient);
        EurekaUtil.initEurekaClient();
    }

}

