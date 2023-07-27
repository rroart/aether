package roart.filesystem.s3;

import roart.common.config.NodeConfig;
import roart.common.constants.FileSystemConstants;
import roart.filesystem.FileSystemAbstractController;
import roart.filesystem.FileSystemOperations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class S3Controller extends FileSystemAbstractController {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(S3Controller.class, args);
	}

	@Override
	protected FileSystemOperations createOperations(String configname, String configid, NodeConfig nodeConf) {
		return new S3(configname, configid, nodeConf);
	}
	
        @Override
        protected String getFs() {
            return FileSystemConstants.S3TYPE;
        }
}
