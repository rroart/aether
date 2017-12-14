package roart.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.docker.api.model.MountPoint;
import io.fabric8.kubernetes.api.Controller;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceStatus;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.DeploymentConfigStatus;
import io.fabric8.openshift.api.model.ImageStream;
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

    public String start(String name, String imageName, String addr, String repo, String namespace) throws IOException {
        //DockerHubUtil.dockermethod();
        Object[] os = new Object[4];
     DockerUtil.method(imageName, repo, namespace, os);

        createDC(name, imageName, repo, namespace, os);
            log.info("end");
            return null;
   }

    public void createDC(String name, String image, String repo, String namespace, Object[] os) {
        Map<String, String> labels = (Map<String, String>) os[0];
        Map<String, Object> volumes = (Map<String, Object>) os[1];
        Map<String, Object> ports = (Map<String, Object>) os[2];
        List<MountPoint> mounts = (List<MountPoint>) os[3];
        Map<String, String> labelsApp = new HashMap<>();
        labelsApp.put("app", name);
        labelsApp.put("deploymentconfig", name);
        /*
        Map<String, String> labelsApp2 = new HashMap<>();
        labelsApp2.put(name, project);
        Map<String, String> selector2 = new HashMap<>();
        selector2.put(name, project);
        */
        
        ObjectMeta metaApp = Fabric8Util.createObjectMeta(name, labelsApp);
      
        Map<String, String> selector = new HashMap<>();
        selector.put("app", name);
        selector.put("deploymentconfig", name);

        ContainerPort containerPort = Fabric8Util.createContainerPort("TCP", 8001);
        List<ContainerPort> cports = new ArrayList<>();
        for(String key : ports.keySet()) {
            String[] spl = key.split("/");
            String proto = spl[1].toUpperCase();
            Integer port = new Integer(spl[0]);
            ContainerPort cport = Fabric8Util.createContainerPort(proto, port);
            cports.add(cport);
        }
        //ports.add(containerPort);
        List<String> names = new ArrayList<>();
        names.add(name);
        Container container = Fabric8Util.createContainer(name, image, cports, repo, namespace);
        List<Container> containers = new ArrayList<>();
        containers.add(container);

        Volume volume = null; //Fabric8Util.createVolume(name);
        
        ImageStream is = Fabric8Util.createImageStream(name, image, metaApp, repo, namespace);
        
        DeploymentConfig dc = new DeploymentConfigBuilder()
                .withMetadata(metaApp)
                .withNewSpec()
                .withReplicas(1)
                .withSelector(selector)
                .withNewStrategy()
                .withNewResources()
               .endResources()
               .endStrategy()
            .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", name)
                .addToLabels("deploymentconfig", name)
                .endMetadata()
                .withNewSpec()
                .withContainers(containers)
                //.withVolumes(volume)
                .endSpec()
                .endTemplate()
                .withTest(false)
                .addNewTrigger()
                .withType("ConfigChange")
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
                /*
                .addNewTrigger()
                .withType("ImageChange")
                .withNewImageChangeParams()
                .withAutomatic(true)
                .withContainerNames(name)
                .withNewFrom()
                .withName(image  + ":latest")
                .endFrom()
                .endImageChangeParams()
                .endTrigger()
                */
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
        
        DeploymentConfigStatus s = osClient
            .deploymentConfigs()
            .inNamespace(project)
            .createOrReplace(dc).getStatus();
        
        ServiceStatus s2 = osClient
        .services()
        .inNamespace(project)
        .createOrReplace(srv) .getStatus();
        
        //Deployment dep = new Deployment(); //DeploymentBuilder().build;
        System.out.println(s.toString());
        System.out.println(s2.toString());
        
        
        Controller cont = new Controller(osClient);
        
        System.out.println("here10");

    }
}
