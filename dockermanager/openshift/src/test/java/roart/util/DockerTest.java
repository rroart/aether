package roart.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;


public class DockerTest {
	//public IOpenShiftConnection connection;
	String project = "myproject";
	 	
	@Before
    public void setUp() {
    	System.out.println("setup done");
	}

    @After
    public void after() {
     	System.out.println("after test");
    	//IUser user = user = connection.getUser();
    }

    @Test
    public void t5() {
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
    	//System.out.println("h0");
    		DockerClient docker = DockerClientBuilder.getInstance(config).build();
    	       System.out.println("h1");
   		docker.pingCmd().exec();
        System.out.println("h2");
    		for (Network n : docker.listNetworksCmd().exec()) {
    			System.out.println("net " + n.getName());
    		}
    		for (Image n : docker.listImagesCmd().exec()) {
    			System.out.println("id " + n.getId());
    		}
    		for (Container n : docker.listContainersCmd().exec()) {
    			System.out.println("c " + n.getId());
    		}
    		//docker.
    		System.out.println("end");
   }
}
