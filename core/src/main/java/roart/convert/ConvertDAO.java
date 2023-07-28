package roart.convert;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import roart.common.config.Connector;
import roart.common.config.Converter;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.IndexFiles;
import roart.common.util.JsonUtil;
import roart.common.webflux.WebFluxUtil;
import roart.eureka.util.EurekaUtil;
import roart.service.ControlService;

public class ConvertDAO {

    private NodeConfig nodeConf;
    
    public ConvertDAO(NodeConfig nodeConf) {
        super();
        this.nodeConf = nodeConf;
    }

    public InmemoryMessage convert(Converter converter, InmemoryMessage message, Map<String, String> metadata, String filename, IndexFiles index) {
        ConvertParam param = new ConvertParam();
        configureParam(param);
        param.message = message;
        param.converter = converter;
        param.filename = filename;
        ConvertResult result = EurekaUtil.sendMe(ConvertResult.class, param, converter.getName().toUpperCase(), EurekaConstants.CONVERT, nodeConf);
        if (result == null) {
            return null;
        }
        // get md from Tika and use it, even if Tika fails
        if (result.metadata != null) {
            metadata.putAll(result.metadata);
        }
        if (result.error != null) {
            index.setFailedreason(result.error);
        }
        if (result.message == null) {
            return null;
        }
        return result.message;
        //Inmemory inmemory = InmemoryFactory.get(Constants.HAZELCAST, null, null);
        //String newparam = inmemory.read(result.message);
        ////inmemory.delete(message);
        //return newparam;
    }

    private void configureParam(ConvertParam param) {
        param.configname = ControlService.getConfigName();
        param.configid = ControlService.getConfigId();
        param.iconf = ControlService.iconf;
        param.iserver = nodeConf.getInmemoryServer();
        if (Constants.REDIS.equals(nodeConf.getInmemoryServer())) {
            param.iconnection = nodeConf.getInmemoryRedis();
        }
    }

}
