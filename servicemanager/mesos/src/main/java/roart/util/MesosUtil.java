package roart.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

public class MesosUtil {
    private static Logger log = LoggerFactory.getLogger(MesosUtil.class);
    private String project = "myproject";

    public MesosUtil() {
        super();
    }

    public MesosUtil(String project) {
        this.project = project;
        log.info("setup done");
    }

    public String start(String imageName, String eurekaURI) throws JsonProcessingException {
        DockerClientConfig config;
        config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerClient docker = DockerClientBuilder.getInstance(config).build();
        docker.pingCmd().exec();
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
            log.error("Searchlist for {} is not size 1, {}", imageName, searchList.size());
            log.error("Searchlist ", searchList);
        }
        InspectImageResponse inspect = null;
        try {
            inspect = docker.inspectImageCmd(imageName).exec();
        } catch (NotFoundException e) {
            log.error("Not found ", e);
        }
        List<Image> images = docker.listImagesCmd().withImageNameFilter(imageName).exec();
        if (inspect != null || images.size() != 1) {
            for (Image image : docker.listImagesCmd().withImageNameFilter(imageName).exec()) {
                String imageString = image.toString();
                log.info("Image {} {}", image.getId(), imageString);
            }
            Image image = images.get(0);
            Container container = null;
            for (Container cont : docker.listContainersCmd().withShowAll(true).exec()) {
                if (image.getId().equals(cont.getImageId())) {
                    container = cont;
                    break;
                }
            }
            ContainerConfig cont = inspect.getContainerConfig();

            MarathonUtil marathonUtil = new MarathonUtil(System.getenv("MARATHON_URL"));
            ObjectNode param = marathonUtil.createMarathonJson(imageName, imageName, 1, 4096, 1, eurekaURI);
            String res = marathonUtil.postMe(String.class, param);
            log.info("Marathon output: {}", res);

        } else {
            log.error("Image not found {}", imageName);
        }
        log.info("end");
        return null;
    }
}
