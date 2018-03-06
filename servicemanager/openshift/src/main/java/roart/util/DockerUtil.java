package roart.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.docker.api.model.AuthConfig;
import io.fabric8.docker.api.model.AuthConfigBuilder;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.openshift.client.OpenShiftClient;

public class DockerUtil {
    private static Logger log = LoggerFactory.getLogger(OpenshiftUtil.class);

    public static boolean inspectTagPush(String name, String repo, String namespace, Object[] os, String openshift, String dockerCertPath, OpenShiftClient osClient) throws IOException, InterruptedException {
        String password = osClient.getConfiguration().getOauthToken();
        log.info("Token {}", password);
        log.info("Repo {}", repo);
        AuthConfig authConfig = new AuthConfigBuilder()
                .withUsername("developer")
                .withPassword(password)
                .build();
        System.setProperty(Config.DOCKER_CERT_PATH_SYSTEM_PROPERTY, dockerCertPath);
        Config config4 = new ConfigBuilder()
                .withDockerUrl("https://" + openshift + ":2376")
                .addToAuthConfigs(repo, authConfig)
                .build();

        DockerClient dClient = new DefaultDockerClient(config4);
        
        // wait with this inspect
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
        /*
        ContainerInspect inspect = dClient.container().withName(name).inspect();
        System.out.println(inspect);
        Map<String, String> labels = inspect.getConfig().getLabels();
        Map<String, Object> volumes = inspect.getConfig().getVolumes();
        Map<String, Object> ports = inspect.getConfig().getExposedPorts();
        List<MountPoint> mounts = inspect.getMounts();
        os[0] = labels;
        os[1] = volumes;
        os[2] = ports;
        os[3] = mounts;
        */

        Fabric8Util.dockerTag(dClient, name, repo, namespace);
        return Fabric8Util.dockerPush(dClient, name, repo, namespace);
    }
}
