package roart.convert;

import java.util.HashMap;
import java.util.Map;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
//@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
public abstract class ConvertAbstractController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static Map<String, ConvertAbstract> convertMap = new HashMap();

	protected abstract ConvertAbstract createConvert(String nodename, NodeConfig nodeConf);

	private ConvertAbstract getConvert(String nodename, NodeConfig nodeConf) {
		ConvertAbstract convert = convertMap.get(nodename);
		if (convert == null) {
			convert = createConvert(nodename, nodeConf);
			convertMap.put(nodename, convert);
		}
		return convert;
	}

	@RequestMapping(value = "/" + EurekaConstants.CONVERT,
			method = RequestMethod.POST)
	public ConvertResult processSearch(@RequestBody ConvertParam param)
			throws Exception {
		ConvertAbstract convert = getConvert(param.configname, param.conf);
		ConvertResult ret = convert.convert(param);
		return ret;
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConvertAbstractController.class, args);
	}
}
