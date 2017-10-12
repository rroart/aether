package roart.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
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

public class UtilTest {

    KubernetesClient client;
    OpenShiftClient osClient;
    KubernetesClient kubernetes = new DefaultKubernetesClient();
    String project = "myproject";
    
    @Test
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
        Map<String, String> labels = new HashMap<>();
        labels.put("build", name);
        ImageStream isDebian = new ImageStreamBuilder()
                .withNewMetadata()
                .withName(debian)
                .addToLabels("build", debian)
                .withLabels(labels)
                .endMetadata()
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
        ImageStream isTFPredict = new ImageStreamBuilder()
                .withNewMetadata()
                .withName(name)
                .addToLabels("build", debian)
                .withLabels(labels)
                .endMetadata()
                .withNewSpec()
                .endSpec()
                .build();
        BuildConfig bc = new BuildConfigBuilder()
                .withNewMetadata()
                .withName(name)
                .addToLabels("build", name)
                .endMetadata()
                .withNewSpec()
                .withNewOutput()
                .withNewTo()
                .withKind("ImageStreamTag")
                .withName(name + ":latest")
                .endTo()
                .endOutput()
                .withNewSource()
                .withType("Binary")
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
        DeploymentConfig dc = new DeploymentConfigBuilder()
                .withNewMetadata()
                .addToLabels("app", name)
                .withName(name)
                .endMetadata()
                .withNewSpec()
                .addToSelector("app", name)
                .addToSelector("deploymentconfig", name)
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", name)
                .addToLabels("deploymentconfig", name)
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withImage("172.30.1.1:5000/" + name + ":latest")
                .withName(name)
                .addNewPort()
                .withContainerPort(8001)
                .withProtocol("TCP")
                .endPort()
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
        Service srv = new ServiceBuilder()
                .withNewMetadata()
                .addToLabels("app", name)
                .withName(name)
                .endMetadata()
                .withNewSpec()
                .addNewPort()
                .withName("8001-tcp")
                .withPort(8001)
                .withProtocol("TCP")
                .withNewTargetPort()
                .withIntVal(8001)
                .endTargetPort()
                .endPort()
                .addToSelector("app", name)
                .addToSelector("deploymentconfig", name)
                .endSpec()
                .build();
         osClient.imageStreams().createOrReplace(isDebian);
        osClient.imageStreams().createOrReplace(isTFPredict);
        System.out.println("del " + osClient.buildConfigs().inNamespace(project).withName(bc.getMetadata().getName()).delete());
        osClient.buildConfigs().inNamespace(project).createOrReplace(bc);
        osClient.buildConfigs().inNamespace(project).withName(bc.getMetadata().getName()).instantiate(new BuildRequestBuilder()
                .withNewMetadata().withName(bc.getMetadata().getName()).endMetadata()
                .build());
    }
}
