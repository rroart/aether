package roart.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

public class MarathonUtil {
    private static Logger log = LoggerFactory.getLogger(MarathonUtil.class);
    
    private String url;
    
    public MarathonUtil(String string) {
        this.url = string;
    }

    public ObjectNode createMarathonJson(String id, String image, int instances, int memory, double cpus, String eurekaURI) throws JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        
        ObjectNode docker = mapper.createObjectNode();
        docker.put("image",  image);
        ObjectNode container = mapper.createObjectNode();
        container.set("docker", docker);
        container.put("type", "DOCKER");

        ObjectNode root = mapper.createObjectNode();

        root.put("id", id);
        root.set("container", container);

        root.put("instances", instances);
        root.put("mem", memory);
        root.put("cpus", cpus);
        ObjectNode env = mapper.createObjectNode();
        env.put("EUREKA_SERVER_URI", eurekaURI);
        root.set("env", env);
        String json = mapper.writeValueAsString(root);
        log.info("JSON {}", json);
        return root;
    }

    public ArrayNode createMarathonJsonArray(String id, String image, int instances, int memory, double cpus, String eurekaURI) throws JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = createMarathonJson(id, image, instances, memory, cpus, eurekaURI);
        ArrayNode rootArray = mapper.createArrayNode();
        rootArray.add(root);
        return rootArray;
    }
    
    public <T> T putMe(Object param) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        headers.setAll(map);

        HttpEntity<?> request = new HttpEntity<>(param, headers);
        RestTemplate rt = new RestTemplate();
        
        rt.put(url, request);

        return null;
    }

    public <T> T postMe(Class<T> myclass, Object param) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        headers.setAll(map);

        HttpEntity<?> request = new HttpEntity<>(param, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<T> regr = rt.postForEntity(url, request, myclass);
        return regr.getBody();
    }

    // not yet used
    public void createVolume() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode volumes = mapper.createObjectNode();
        ObjectNode volume = mapper.createObjectNode();
        volume.put("containerPath", "/");
        volume.put("hostPath", "/");
        volume.put("mode", "RW");
        //container.set("volumes", volumes);
    }
}
