package roart.util;

import java.util.List;

import io.fabric8.docker.api.model.Container;
import io.fabric8.docker.api.model.ContainerInspect;
import io.fabric8.docker.api.model.Image;
import io.fabric8.docker.api.model.ImageInspect;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.container.LimitSinceBeforeSizeFiltersAllRunningInterface;
import io.fabric8.docker.dsl.image.FilterFiltersAllImagesEndInterface;

public class DockerUtil {

    public static void method() {
        System.setProperty(Config.DOCKER_CERT_PATH_SYSTEM_PROPERTY, "/home/roart/.minishift/certs");
        //System.set
        Config config4 = new ConfigBuilder()
                .withDockerUrl("https://192.168.42.56:2376")
                //.withCaCertData("/home/roart/.minishift/certs/)
                //.withDockerUrl("tcp://docker.io")
                .build();

        DockerClient dClient = new DefaultDockerClient(config4);
        
        FilterFiltersAllImagesEndInterface<List<Image>> ims = dClient.image().list();
        for (Image i : ims.allImages()) {
            System.out.println(i.getId() + " " + i.getLabels() + i.getRepoTags());
            System.out.println("I " + i.getAdditionalProperties());
        }
        LimitSinceBeforeSizeFiltersAllRunningInterface<List<Container>> conts = dClient.container().list();
        for (Container i : conts.all()) {
            System.out.println("id " + i.getId() + " " + i.getImage() + " " + i.getNames());
        }
        //dClient.image().
        ImageInspect ii = dClient.image().withName("6844c48bc4bcdc8491d4073371889e0e1959ceb701dbdb3eb8031b013247d104").inspect();
        System.out.println(ii);
        ContainerInspect inspect = dClient.container().withName("/registry").inspect();
        System.out.println(inspect);
        //inspect.getHostConfig().
        
    }
}
