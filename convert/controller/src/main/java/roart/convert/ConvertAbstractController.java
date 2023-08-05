package roart.convert;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryUtil;
import roart.common.util.IOUtil;
import roart.common.util.JsonUtil;

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

    protected abstract ConvertAbstract createConvert(String configname, String configid, NodeConfig nodeConf);

    private ConvertAbstract getConvert(ConvertParam param) {
        ConvertAbstract convert = convertMap.get(param.configid);
        if (convert == null) {
            NodeConfig nodeConf = null;
            if (param.conf == null) {
                Inmemory inmemory = InmemoryFactory.get(param.iserver, param.configname, param.iconnection);
                try (InputStream contentStream = inmemory.getInputStream(param.iconf)) {
                    if (InmemoryUtil.validate(param.iconf.getMd5(), contentStream)) {
                        String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArray1G(inmemory.getInputStream(param.iconf)));
                        nodeConf = JsonUtil.convertnostrip(content, NodeConfig.class);
                    }
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
                param.conf = nodeConf;
            } else {
                nodeConf = param.conf;
            }
            convert = createConvert(param.configname, param.configid, nodeConf);
            convertMap.put(param.configid, convert);
        }
        return convert;
    }

    @RequestMapping(value = "/" + EurekaConstants.CONVERT,
            method = RequestMethod.POST)
    public ConvertResult processSearch(@RequestBody ConvertParam param)
            throws Exception {
        ConvertAbstract convert = getConvert(param);
        ConvertResult ret = convert.convert(param);
        return ret;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConvertAbstractController.class, args);
    }

    public abstract String getQueueName();
}
