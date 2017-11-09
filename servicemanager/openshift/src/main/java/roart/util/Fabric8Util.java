package roart.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.fabric8.docker.api.model.Port;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.openshift.api.model.ImageStreamStatus;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;

public class Fabric8Util {

    KubernetesClient client;
    OpenShiftClient osClient;
    KubernetesClient kubernetes = new DefaultKubernetesClient();

    public Fabric8Util() {
        client = new DefaultKubernetesClient();
        osClient = new DefaultOpenShiftClient();
    }

    public void method() {

    }

    public static ServicePort createServicePort(String protocol, Integer port, Integer targetPort) {
        ServicePort servicePort = new ServicePort();
        servicePort.setName(protocol.toLowerCase() + "-" + port);
        servicePort.setPort(port);
        servicePort.setProtocol(protocol);
        servicePort.setTargetPort(new IntOrString(targetPort));
        return servicePort;
    }

    public static ContainerPort createContainerPort(String protocol, Integer port) {
        ContainerPort containerPort = new ContainerPort();
        //containerPort.setName(protocol + "-" + port);
        containerPort.setContainerPort(port);
        containerPort.setProtocol(protocol);
        return containerPort;
    }

    public static ObjectMeta createObjectMeta(String name, Map<String, String> labels) {
        ObjectMeta meta = new ObjectMeta();
        meta.setName(name);
        meta.setLabels(labels);
        return meta;
    }

    public static ImageStream createImageStream(String name, String image, ObjectMeta metadata, String repo) {
        ImageStreamStatus status = new ImageStreamStatus();
        status.setDockerImageRepository(repo + image);
        ImageStream is = new ImageStream();
        is.setMetadata(metadata);
        is.setStatus(status);
        return is;
    }

    public static Container createContainer(String name, String image, List<ContainerPort> ports, String repo) {
        Container container = new Container();
        container.setName(name);
        container.setImage(repo + image);
        container.setPorts(ports);
        /*
        ResourceRequirements resources = new ResourceRequirements();
        container.setResources(resources);
         */
        return container;
    }

    public static boolean dockerTag(DockerClient client, String name, String repo, String tag) {
        boolean bool = client
                .image()
                .withName(name)
                .tag()
                .inRepository(repo + name)
                .force()
                .withTagName("latest")
//                .withTagName("default")
                ;
        return bool;
    }

    public static boolean dockerPush(DockerClient client, String name, String repo, String tag) throws IOException {
        client
        .image()
        .withName(name)
        .push()
        .toRegistry()
        .close();
        return true;
    }
}
