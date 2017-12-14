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
    public void t5() {
        MarathonUtil mu = new MarathonUtil();
        String url = mu.adr();
        String image = "aether-hbase";
        ObjectNode param = mu.mehod("myid", image);
        String res = MarathonUtil.sendMe(String.class, param, url);
        System.out.println(res);
    }
    
}