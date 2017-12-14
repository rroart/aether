package roart.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.fabric8.docker.api.model.Container;
import io.fabric8.docker.api.model.ContainerInspect;
import io.fabric8.docker.api.model.Image;
import io.fabric8.docker.api.model.ImageInspect;
import io.fabric8.docker.api.model.MountPoint;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.container.LimitSinceBeforeSizeFiltersAllRunningInterface;
import io.fabric8.docker.dsl.image.FilterFiltersAllImagesEndInterface;

public class DockerUtil {

    public static void method(String name, String repo, String namespace, Object os[]) throws IOException {
        System.setProperty(Config.DOCKER_CERT_PATH_SYSTEM_PROPERTY, "/home/roart/.minishift/certs");
        //System.set
        Config config4 = new ConfigBuilder()
                .withDockerUrl("https://192.168.42.56:2376")
                //.withCaCertData("/home/roart/.minishift/certs/)
                //.withDockerUrl("tcp://docker.io")
                .build();

        DockerClient dClient = new DefaultDockerClient(config4);
        /*
        FilterFiltersAllImagesEndInterface<List<Image>> ims = dClient.image().list();
        for (Image i : ims.allImages()) {
            System.out.println(i.getId() + " " + i.getLabels() + i.getRepoTags());
            System.out.println("I " + i + " " + i.getAdditionalProperties());
        }
        LimitSinceBeforeSizeFiltersAllRunningInterface<List<Container>> conts = dClient.container().list();
        for (Container i : conts.all()) {
            System.out.println("id " + i.getId() + " " + i.getImage() + " " + i.getNames());
        }
        //dClient.image().
        ImageInspect ii = dClient.image().withName("6844c48bc4bcdc8491d4073371889e0e1959ceb701dbdb3eb8031b013247d104").inspect();
        System.out.println(ii);
        */
        ContainerInspect inspect = dClient.container().withName("/registry").inspect();
        System.out.println(inspect);
        Map<String, String> labels = inspect.getConfig().getLabels();
        Map<String, Object> volumes = inspect.getConfig().getVolumes();
        Map<String, Object> ports = inspect.getConfig().getExposedPorts();
        List<MountPoint> mounts = inspect.getMounts();
        os[0] = labels;
        os[1] = volumes;
        os[2] = ports;
        os[3] = mounts;
        Fabric8Util.dockerTag(dClient, name, repo, namespace, name);
        Fabric8Util.dockerPush(dClient, name, repo, namespace, name);
        //inspect.getHostConfig().
        
    }
}
