package roart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {

    public static void main(String[] args) throws Exception {
java.util.Properties p = System.getProperties();
java.util.Enumeration keys = p.keys();
while (keys.hasMoreElements()) {
    String key = (String)keys.nextElement();
    String value = (String)p.get(key);
    System.out.println(key + ": " + value);
}
System.setProperty("logfile", EurekaApplication.class.getSimpleName() + ".log");
	SpringApplication.run(EurekaApplication.class, args);
    }
}
