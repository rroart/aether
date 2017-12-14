package roart.util;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerConfig;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

public class DockerThread {
    private static Logger log = LoggerFactory.getLogger(DockerThread.class);
    String project = "myproject";
    
     public DockerThread() {
        try {
        Response r = null;
        // compile canary
        r.close();
        } catch (Exception e) {
            
        }
        log.info("setup done");
    }

    public String start(String imageName, String addr) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.42.56:2376")
                
                .withDockerTlsVerify(true)
                .withDockerCertPath("/home/roart/.minishift/certs")
                //.withDockerConfig("/home/roart/.docker")
                //.withApiVersion("1.23")
                
                .withRegistryUrl("http://192.168.42.56:5000/v1/")
                /*
                .withRegistryUsername("dockeruser")
                .withRegistryPassword("ilovedocker")
                .withRegistryEmail("dockeruser@github.com")
                */
                .build();
        config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        log.info("h0");
            DockerClient docker = DockerClientBuilder.getInstance(config).build();
               log.info("h1");
        docker.pingCmd().exec();
        log.info("h2");
        /*
            for (Network n : docker.listNetworksCmd().exec()) {
                log.info("net " + n.getName());
            }
            for (Image n : docker.listImagesCmd().exec()) {
                log.info("id " + n.getId() +  " " + Arrays.asList(n.getRepoTags()));
                InspectImageResponse insp = docker.inspectImageCmd(n.getId()).exec();
                log.info("" + insp);
                //insp.get
            }
            for (Container n : docker.listContainersCmd().exec()) {
                log.info("c " + n.getId());
            }
            */
            List<Image> searchList = docker.listImagesCmd().withImageNameFilter(imageName).exec();
            if (searchList.isEmpty() || searchList.size() > 1) {
                log.error("searchlist for " + imageName + " is not size 1, " + searchList.size());
                log.error(""+searchList);
                //return null;
            }
            //Image image = searchList.get(0);
            //log.info("image " + image);
            InspectImageResponse inspect = null;
            try {
                inspect = docker.inspectImageCmd(imageName).exec();
            } catch (NotFoundException e) {
                
            }
            List<Image> images = docker.listImagesCmd().withImageNameFilter(imageName).exec();
            if (inspect != null || images.size() != 1) {
                for (Image image : docker.listImagesCmd().withImageNameFilter(imageName).exec()) {
                    log.info("im " + image.getId() + " "    + image.toString());
                }
                Image image = images.get(0);
                Container container = null;
                for (Container cont : docker.listContainersCmd().withShowAll(true).exec()) {
                    if (image.getId().equals(cont.getImageId())) {
                        container = cont;
                        break;
                    }
                    //log.info("conts " + cont.getId() + " " + cont.getImageId());
                }
                //log.info("siz " + docker.listContainersCmd().exec().size());
                ContainerConfig cont = inspect.getContainerConfig();
                //cont.getHealthcheck().get
                String contId = null; //container.getId();
                 if (container != null) {
                     if (container.getStatus().startsWith("Up")) {
                         return null;
                     }
                     contId = container.getId();
                   //contId = cont.getHostName();
                    log.info("cmd " + contId);
                } else {
                    CreateContainerCmd cmd = docker.createContainerCmd(imageName).withEnv("EUREKA_SERVER_URI=" + System.getenv("EUREKA_SERVER_URI") + ";");
                    CreateContainerResponse createResponse = cmd.exec();
                    contId = createResponse.getId();
                    log.info("id " + contId);
                }
                 try {
                     //docker.listContainersCmd().with
                docker.startContainerCmd(contId).exec();
                 } catch (Exception e) {
                     log.error("Exception", e);
                 }
                //ret.g
            } else {
                log.error("image not found " + imageName);
            }
            //inspect.get
            log.info("end");
            return null;
   }
}
