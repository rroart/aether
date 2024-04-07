package roart.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.util.InmemoryUtil;
import roart.common.model.ConfigParam;
import roart.common.service.ServiceResult;
import roart.common.util.IOUtil;
import roart.common.util.JsonUtil;
import roart.config.MyXMLConfig;
import roart.common.config.NodeConfig;

@RestController
public class ServiceController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static Map<String, NodeConfig> confMap = new HashMap<>();

    @RequestMapping(value = "/" + EurekaConstants.SETCONFIG,
            method = RequestMethod.POST)
    public ServiceResult setConfig(@RequestBody ConfigParam param) {
        ServiceResult result = new ServiceResult();
        try {
            String key = param.getConfigid();
            NodeConfig conf = confMap.get(key);
            if (conf == null) {
                conf = getNodeConf(param);
                confMap.put(key, conf);
                MyXMLConfig.instance(conf);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    private NodeConfig getNodeConf(ConfigParam param) {
        NodeConfig nodeConf = null;
        Inmemory inmemory = InmemoryFactory.get(param.getIserver(), param.getIconnection(), param.getIconnection());
        try (InputStream contentStream = inmemory.getInputStream(param.getIconf())) {
            if (InmemoryUtil.validate(param.getIconf().getMd5(), contentStream)) {
                String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArray1G(inmemory.getInputStream(param.getIconf())));
                nodeConf = JsonUtil.convertnostrip(content, NodeConfig.class);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return nodeConf;
    }
}
