package roart.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import com.netflix.discovery.DiscoveryClient;

import roart.eureka.util.EurekaUtil;

@SpringBootApplication
@EnableDiscoveryClient
public class WebApplication implements CommandLineRunner {

    @Autowired
    public static DiscoveryClient discoveryClient; // = null;

    public static void main(String[] args) throws Exception {
        System.out.println("haha " + discoveryClient);
        try {
        SpringApplication.run(WebApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("haha1 " + discoveryClient);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        System.out.println("haha2 " + discoveryClient);
        EurekaUtil.initEurekaClient();
    }

}

