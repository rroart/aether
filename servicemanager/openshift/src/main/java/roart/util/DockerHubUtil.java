package roart.util;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

public class DockerHubUtil {
    private static Logger log = LoggerFactory.getLogger(OpenshiftUtil.class);

    static void dockermethod() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.42.80:2376")
                
                .withDockerTlsVerify(true)
                .withDockerCertPath("/home/roart/.minishift/certs")
                //.withDockerConfig("/home/roart/.docker")
                //.withApiVersion("1.23")
                
                //.withRegistryUrl("http://192.168.42.80:5000/v1/")
                .withRegistryUrl("http://172.30.1.1:5000/myproject/")
                /*
                .withRegistryUsername("dockeruser")
                .withRegistryPassword("ilovedocker")
                .withRegistryEmail("dockeruser@github.com")
                */
                .build();
        //config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        log.info("h0");
            DockerClient docker = DockerClientBuilder.getInstance(config).build();
               log.info("h1");
        //docker.pingCmd().exec();
        log.info("h2");
         //docker.authConfig().withRegistryAddress("http://172.30.1.1:5000/myproject/");
       System.out.println(docker.authConfig().getRegistryAddress());
        /*
            for (Network n : docker.listNetworksCmd().exec()) {
                log.info("net " + n.getName());
            }
            */
            for (Image n : docker.listImagesCmd().exec()) {
                System.out.println("id " + n.getId() +  " " + Arrays.asList(n.getRepoTags()));
                InspectImageResponse insp = docker.inspectImageCmd(n.getId()).exec();
                System.out.println("" + insp);
                ///insp.getContainerConfig().ge
                //insp.get
            }
            /*
            for (Container n : docker.listContainersCmd().exec()) {
                log.info("c " + n.getId());
            }
            */
            //docker.tag
    }
    
}
