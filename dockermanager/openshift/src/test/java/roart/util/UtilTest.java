package roart.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.BuildConfig;
import io.fabric8.openshift.api.model.BuildConfigBuilder;
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

    @Test
    public void t1() {
        client = new DefaultKubernetesClient();
        osClient = new DefaultOpenShiftClient();
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
                .withType("ConfigChange")
                .endTrigger()
                .endSpec()
                .build();
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
    }
}
