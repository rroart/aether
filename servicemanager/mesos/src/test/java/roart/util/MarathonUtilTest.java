package roart.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.netflix.discovery.util.EurekaUtils;

import javax.ws.rs.core.Response;

public class MarathonUtilTest {

    @Before
    public void setUp() {
        try {
        Response r = null;
        // compile canary
        r.close();
        } catch (Exception e) {
            
        }
        System.out.println("setup done");
    }
    
    @After
    public void after() {
        System.out.println("after test");
        //IUser user = user = connection.getUser();
    }

    @Test
    public void t5() throws JsonProcessingException {
        Runnable eureka = new JarThread("/home/roart/src/aethermicro/eureka/target/aether-eureka-0.10-SNAPSHOT.jar");
        new Thread(eureka).start();
        MarathonUtil mu = new MarathonUtil("http://localhost:18082/v2/apps");
         String image = "aether-elastic";
        //image = "debian";
        //image = "mesosphere/chronos";
        //image = "aether-local";
        ArrayNode param = mu.createMarathonJsonArray(image, image, 1, 4096, 1, "http://192.168.0.100:8761/eureka");
        String res = mu.putMe(param);
        System.out.println(res);
    }
    
}