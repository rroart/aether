package roart.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.docker.api.model.MountPoint;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.DeploymentConfigStatus;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;

public class OpenshiftUtil {
    private static Logger log = LoggerFactory.getLogger(OpenshiftUtil.class);
    String project = "myproject";
    KubernetesClient client;
    OpenShiftClient osClient;
    KubernetesClient kubernetes = new DefaultKubernetesClient();

    public OpenshiftUtil() {
        Config conf2 = new ConfigBuilder()
                .withUsername("developer")
                .withPassword("developer")
                .build();
        client = new DefaultKubernetesClient(conf2);
        osClient = new DefaultOpenShiftClient(conf2);
        log.info("setup done");
    }

    public String start(String name, String imageName, String addr, String repo, String namespace, String openshift, String dockerCertPath) throws IOException, InterruptedException {
        Object[] os = new Object[4];
        boolean pushed = DockerUtil.inspectTagPush(imageName, repo, namespace, os, openshift, dockerCertPath, osClient);

        if (pushed) {
            createDC(name, imageName, repo, namespace, os, addr);
        }
        return null;
    }

    public void createDC(String name, String image, String repo, String namespace, Object[] os, String eurekaURI) {
        // handle most of these later
        Map<String, String> labels = (Map<String, String>) os[0];
        Map<String, Object> volumes = (Map<String, Object>) os[1];
        Map<String, Object> ports = (Map<String, Object>) os[2];
        List<MountPoint> mounts = (List<MountPoint>) os[3];
        Map<String, String> labelsApp = new HashMap<>();
        labelsApp.put("app", name);
        labelsApp.put("deploymentconfig", name);

        ObjectMeta metaApp = Fabric8Util.createObjectMeta(name, labelsApp);

        Map<String, String> selector = new HashMap<>();
        selector.put("app", name);
        selector.put("deploymentconfig", name);

        // handle later...
        ContainerPort containerPort = Fabric8Util.createContainerPort("TCP", 8001);
        List<ContainerPort> cports = new ArrayList<>();
        if (ports != null) {
            for(String key : ports.keySet()) {
                String[] spl = key.split("/");
                String proto = spl[1].toUpperCase();
                Integer port = new Integer(spl[0]);
                ContainerPort cport = Fabric8Util.createContainerPort(proto, port);
                cports.add(cport);
            }
        }
        //ports.add(containerPort);

        List<String> names = new ArrayList<>();
        names.add(name);
        List<EnvVar> env = createEnv(eurekaURI);

        Container container = Fabric8Util.createContainer(name, image, cports, repo, namespace, env);
        List<Container> containers = new ArrayList<>();
        containers.add(container);

        // handle later...
        Volume volume = null; //Fabric8Util.createVolume(name);

        /*
         // not needed?
        ImageStream is = Fabric8Util.createImageStream(image, metaApp, repo, namespace);
        osClient
        .imageStreams()
        .inNamespace(project)
        .createOrReplace(is);
         */

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
                .withHostNetwork(true)
                .withContainers(containers)
                //.withVolumes(volume)
                .endSpec()
                .endTemplate()
                .withTest(false)
                .addNewTrigger()
                .withType("ConfigChange")
                // keep code in case...
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
                // keep code in case...
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

        // keep code in case...
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

        // handle later...
        /*
        ServicePort servicePort = Fabric8Util.createServicePort("TCP", 8001, 8001);
        System.out.println("here4");
        Service srv = new ServiceBuilder()
                .withMetadata(metaApp)
                .withNewSpec()
          //      .withPorts(servicePort)
                .withSelector(selector)
                .endSpec()
                .build();
         */
        DeploymentConfigStatus s = osClient
                .deploymentConfigs()
                .inNamespace(project)
                .createOrReplace(dc).getStatus();

        // not needed
        /*
        ServiceStatus s2 = osClient
                .services()
                .inNamespace(project)
                .createOrReplace(srv) .getStatus();
        System.out.println(s2.toString());
         */

    }

    private List<EnvVar> createEnv(String eurekaURI) {
        List<EnvVar> env = new ArrayList<>();
        EnvVar var = new EnvVar();
        var.setName("EUREKA_SERVER_URI");
        var.setValue(eurekaURI);
        env.add(var);
        EnvVar var2 = new EnvVar();
        var2.setName("EUREKA_PREFER_IPADDRESS");
        var2.setValue("true");
        env.add(var2);
        return env;
    }
}
