package roart.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.EventListener;
import io.fabric8.docker.dsl.OutputHandle;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.SecurityContext;
import io.fabric8.kubernetes.api.model.SecurityContextBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.openshift.api.model.ImageStreamStatus;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;

public class Fabric8Util {
    private static Logger log = LoggerFactory.getLogger(OpenshiftUtil.class);

    KubernetesClient client;
    OpenShiftClient osClient;
    KubernetesClient kubernetes = new DefaultKubernetesClient();

    public Fabric8Util() {
        client = new DefaultKubernetesClient();
        osClient = new DefaultOpenShiftClient();
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
        containerPort.setName(protocol + "-" + port);
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

    public static ImageStream createImageStream(String image, ObjectMeta metadata, String repo, String namespace) {
        ImageStreamStatus status = new ImageStreamStatus();
        status.setDockerImageRepository(repo + "/" + namespace + "/" + image);
        ImageStream is = new ImageStream();
        is.setMetadata(metadata);
        is.setStatus(status);
        return is;
    }

    public static VolumeMount createMount(String name, String mountPath) {
        VolumeMount mount = new VolumeMount();
        mount.setName(name);
        mount.setMountPath(mountPath);
        return mount;
    }

    public static Volume createVolume(String name) {
        Volume volume = new Volume();
        volume.setName(name);
        //volume.set
        //volume.set
        return volume;
    }

    public static Container createContainer(String name, String image, List<ContainerPort> ports, String repo, String namespace, List<EnvVar> env) {
        Container container = new Container();
        container.setName(name);
        container.setImage(repo + "/" + namespace + "/" + image);
        if (ports != null && !ports.isEmpty()) {
            container.setPorts(ports);
        }
        if (env != null && !env.isEmpty()) {
            container.setEnv(env);
        }
        SecurityContext securityContext = new SecurityContextBuilder()
                .withPrivileged(true)
                .build();
        container.setSecurityContext(securityContext);
        return container;
    }

    public static boolean dockerTag(DockerClient client, String name, String repo, String namespace) {
        return client
                .image()
                .withName(name)
                .tag()
                .inRepository(repo + "/" + namespace + "/" + name)
                .force()
                .withTagName("latest");
    }

    public static boolean dockerPush(DockerClient client, String name, String repo, String namespace) throws IOException, InterruptedException {
        String pushed = repo + "/" + namespace + "/" + name;
        log.info("Try push {}", pushed);
        final CountDownLatch pushDone = new CountDownLatch(1);
        boolean[] success = new boolean[1];
        success[0] = false;

        OutputHandle handle = client
                .image()
                .withName(repo + "/" + namespace + "/" + name)
                .push()
                .usingListener(new EventListener() {
                    @Override
                    public void onSuccess(String message) {
                        log.info("Push success {}", message);
                        pushDone.countDown();
                        success[0] = true;
                    }

                    @Override
                    public void onError(String messsage) {
                        log.info("Push failure {}", messsage);
                        pushDone.countDown();
                    }

                    @Override
                    public void onError(Throwable t) {
                        log.error(Constants.EXCEPTION, t);
                        pushDone.countDown();
                    }

                    @Override
                    public void onEvent(String event) {
                        log.info("Push event {}", event);
                    }
                })            
                .withTag("latest")
                .toRegistry();
        pushDone.await();
        handle.close();
        client.close();
        return success[0];
    }
}
