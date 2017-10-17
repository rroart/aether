package roart.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
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
    }}
