package roart.util;

import java.util.List;

import io.fabric8.docker.api.model.Container;
import io.fabric8.docker.api.model.ContainerInspect;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.container.LimitSinceBeforeSizeFiltersAllRunningInterface;

public class DockerUtil {

    public static void method() {
        Config config4 = new ConfigBuilder()
                //.withDockerUrl("tcp://192.168.42.56:2376")
                .withDockerUrl("tcp://docker.io")
                .build();

        DockerClient dClient = new DefaultDockerClient(config4);
        /*
        LimitSinceBeforeSizeFiltersAllRunningInterface<List<Container>> conts = dClient.container().list();
        for (Container i : conts.all()) {
            System.out.println("id " + i.getId());
        }
        */
        ContainerInspect inspect = dClient.container().withName("mysql").inspect();
        System.out.println(inspect);
        //inspect.getHostConfig().
        
    }
}
