package roart.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MarathonUtil {
    
    public ObjectNode mehod(String id, String image) {
        ObjectMapper mapper = new ObjectMapper();
        
        ObjectNode docker = mapper.createObjectNode();
        docker.put("image",  image);
        ObjectNode container = mapper.createObjectNode();
        container.set("docker", docker);
        container.put("type", "MESOS");
        
        ObjectNode root = mapper.createObjectNode();
        root.put("id", id);
        root.set("container", container);
        root.put("instances", 1);
        return root;
    }
    
    public String adr() {
        return "http://localhost:18081/v2/apps";
    }

    public static <T> T sendMe(Class<T> myclass, Object param, String url) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");

        headers.setAll(map);

        HttpEntity<?> request = new HttpEntity<>(param, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<T> regr = rt.postForEntity(url, request, myclass);
        T result = regr.getBody();
        //log.info("resultme " + regr.getHeaders().size() + " " + regr.toString());
        return result;
    }

}
