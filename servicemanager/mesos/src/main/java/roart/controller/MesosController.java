package roart.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;

import roart.util.JarThread;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class MesosController implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MesosController.class, args);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        Runnable eureka = new JarThread("aether-eureka-0.10-SNAPSHOT.jar", null);
        new Thread(eureka).start();
        Runnable core = new JarThread("aether-core-0.10-SNAPSHOT.jar", args);
        new Thread(core).start();
        Runnable local = new JarThread("aether-local-0.10-SNAPSHOT.jar", null);
        new Thread(local).start();
    }
}
