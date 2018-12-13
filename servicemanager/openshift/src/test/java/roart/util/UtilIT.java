package roart.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.BuildConfig;
import io.fabric8.openshift.api.model.BuildConfigBuilder;
import io.fabric8.openshift.api.model.BuildRequestBuilder;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.openshift.api.model.ImageStreamBuilder;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;

public class UtilIT {

    KubernetesClient client;
    OpenShiftClient osClient;
    KubernetesClient kubernetes = new DefaultKubernetesClient();
    String project = "myproject";
    
    //@Test
    public void t1() {
        Config conf2 = new ConfigBuilder()
                .withUsername("developer")
                .withPassword("developer")
                .build();
        client = new DefaultKubernetesClient(conf2);
        osClient = new DefaultOpenShiftClient(conf2);
        String name = "tensorflow-predict";
        String debian = "debian";
        String debianstretch = "debian:stretch";
        String stretch = "stretch";
        Map<String, String> selector = new HashMap<>();
        selector.put("app", name);
        selector.put("deploymentconfig", name);
        Map<String, String> labelsBuild = new HashMap<>();
        labelsBuild.put("build", name);
        Map<String, String> labelsApp = new HashMap<>();
        labelsApp.put("build", name);
        
        Map<String, String> debianLabels = new HashMap<>();
        debianLabels.put("build", debian);

        ObjectMeta metaDeb = Fabric8Util.createObjectMeta(debian, labelsBuild);
        
        ObjectMeta metaBuild = Fabric8Util.createObjectMeta(name, labelsBuild);
        
        ObjectMeta metaApp = Fabric8Util.createObjectMeta(name, labelsApp);
        
        /*
        Map<String, String> selectorMap = new HashMap<>();
        selectorMap.put("app", name);
        selectorMap.put("deploymentconfig", name);
       */
        System.out.println("here0");
        ImageStream isDebian = new ImageStreamBuilder()
                .withMetadata(metaDeb)
                .withNewSpec()
                .addNewTag()
                .addToAnnotations("openshift.io/imported-from", debianstretch)
                .editOrNewFrom()
                .withKind("DockerImage")
                .withName(debianstretch)
                .endFrom()
                .withName(stretch)
                .endTag()
                .endSpec()
                .build();
        System.out.println("here1");
        ImageStream isTFPredict = new ImageStreamBuilder()
                .withMetadata(metaBuild)
                .withNewSpec()
                .endSpec()
                .build();
        System.out.println("here2");
        BuildConfig bc = new BuildConfigBuilder()
                .withMetadata(metaBuild)
                .withNewSpec()
                .withNewOutput()
                .withNewTo()
                .withKind("ImageStreamTag")
                .withName(name + ":latest")
                .endTo()
                .endOutput()
                .withNewSource()
                .withType("Binary")
                .withNewBinary()
                .withAsFile(null)
                .endBinary()
                /*
                .withType("Dockerfile")
                .withDockerfile("/tmp/Dockerfile")
                */
                .endSource()
                .withNewStrategy()
                .withNewDockerStrategy()
                .withNewFrom()
                .withKind("ImageStreamTag")
                .withName(debianstretch)
                .endFrom()
                .endDockerStrategy()
                .withType("Docker")
                .endStrategy()
                .endSpec()
                .build();
        ContainerPort containerPort = Fabric8Util.createContainerPort("TCP", 8001);
        System.out.println("here3");
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
                .addNewContainer()
                .withImage("172.30.1.1:5000/" + name + ":latest")
                .withName(name)
                .withPorts(containerPort)
                .endContainer()
                .endSpec()
                .endTemplate()
                .withTest(false)
                .addNewTrigger()
                .withType("imagechange")
                .withNewImageChangeParams()
                //.withNewImageChange()
                .withNewFrom()
                .withKind("ImageStreamTag")
                .withName(debianstretch)
                .endFrom()
                .endImageChangeParams()
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
        System.out.println("here5");
         osClient.imageStreams().createOrReplace(isDebian);
         System.out.println("here6");
        osClient.imageStreams().createOrReplace(isTFPredict);
        System.out.println("her7");
        System.out.println("del " + osClient.buildConfigs().inNamespace(project).withName(bc.getMetadata().getName()).delete());
        System.out.println("here8");
        osClient.buildConfigs().inNamespace(project).createOrReplace(bc);
        System.out.println("here9");
        osClient.buildConfigs()
        .inNamespace(project)
        .withName(bc.getMetadata().getName())
        .instantiate(new BuildRequestBuilder()
                .withNewMetadata()
                .withName(bc.getMetadata().getName())
                .endMetadata()
                .build());
        System.out.println("here10");
    }
    
    @Test
public void t3() throws IOException {
        String openshift = "192.168.42.80";
        String repo = "172.30.1.1:5000";
        String namespace = "myproject";
        Object[] os = new Object[4];
    //DockerUtil.method("aether-local", repo, namespace, os, openshift, "/home/roart/.minishift/certs");
}
    @Test
    public void t4() throws IOException, InterruptedException {
        String openshift = "192.168.42.80";
        String repo = "172.30.1.1:5000";
        String namespace = "myproject";
        OpenshiftUtil o = new OpenshiftUtil();
        //o.start("mariadb", "centos/mariadb", null, repo, namespace, openshift);
        //o.start("mysql", "mysql", null, repo, namespace);
        o.start("aether-local", "aether-local", "http://192.168.0.100:8761/eureka", repo, namespace, openshift, "/home/roart/.minishift/certs");
    }
}
