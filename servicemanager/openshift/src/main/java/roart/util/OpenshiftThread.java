package roart.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ContainerConfig;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.BuildRequestBuilder;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;

public class OpenshiftThread {
    private static Logger log = LoggerFactory.getLogger(OpenshiftThread.class);
    String project = "myproject";
    KubernetesClient client;
    OpenShiftClient osClient;
    KubernetesClient kubernetes = new DefaultKubernetesClient();
    
     public OpenshiftThread() {
        try {
        Response r = null;
        // compile canary
        r.close();
        } catch (Exception e) {
            
        }
        Config conf2 = new ConfigBuilder()
                .withUsername("developer")
                .withPassword("developer")
                .build();
        client = new DefaultKubernetesClient(conf2);
        osClient = new DefaultOpenShiftClient(conf2);
        log.info("setup done");
    }

    public String start(String name, String imageName, String addr) {
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
        //docker.pingCmd().exec();
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
        createDC(name, imageName);
            log.info("end");
            return null;
   }
    
    public void createDC(String name, String image) {
        Map<String, String> labelsApp = new HashMap<>();
        labelsApp.put("app", name);
        
        ObjectMeta metaApp = Fabric8Util.createObjectMeta(name, labelsApp);
      
        Map<String, String> selector = new HashMap<>();
        selector.put("app", name);
        selector.put("deploymentconfig", name);

        ContainerPort containerPort = Fabric8Util.createContainerPort("TCP", 8001);
        List<ContainerPort> ports = new ArrayList<>();
        //ports.add(containerPort);
        List<String> names = new ArrayList<>();
        names.add(name);
        Container container = Fabric8Util.createContainer(name, image, ports);
        List<Container> containers = new ArrayList<>();
        containers.add(container);
        
        DeploymentConfig dc = new DeploymentConfigBuilder()
                .withMetadata(metaApp)
                .withNewSpec()
                .withSelector(selector)
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", name)
                .addToLabels("deploymentconfig", name)
                .endMetadata()
                .withNewSpec()
                .withContainers(containers)
                .endSpec()
                .endTemplate()
                .withTest(false)
                .addNewTrigger()
                .withType("configchange")
                /*
                .withNewImageChangeParams()
                //.withNewImageChange()
                .withNewFrom()
                .withKind("ImageStreamTag")
                .withName(debianstretch)
                .endFrom()
                .endImageChangeParams()
                */
                .endTrigger()
                .endSpec()
                .build();
                 
                /*
                .with
                .withNewGeneric()
                .withSecret("secret101")
                .endGeneric()
                .build();
                /*
                .endImageChange()
                */
                /*
                .addNewTrigger()
                .withType("ConfigChange")
                .endTrigger()
                .endSpec()
                .build();
                */
        
        ServicePort servicePort = Fabric8Util.createServicePort("TCP", 8001, 8001);
        System.out.println("here4");
        Service srv = new ServiceBuilder()
                .withMetadata(metaApp)
                .withNewSpec()
                .withPorts(servicePort)
                .withSelector(selector)
                .endSpec()
                .build();
        
        osClient
            .deploymentConfigs()
            .inNamespace(project)
            .createOrReplace(dc);
        
        osClient
        .services()
        .inNamespace(project)
        .createOrReplace(srv);
        
        //Deployment dep = new Deployment(); //DeploymentBuilder().build;
        
        System.out.println("here10");

    }
}
