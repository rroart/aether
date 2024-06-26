package roart.convert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.Connector;
import roart.common.config.Converter;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.constants.OperationConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.IndexFiles;
import roart.common.queue.QueueElement;
import roart.common.util.JsonUtil;
import roart.common.webflux.WebFluxUtil;
import roart.content.ConvertHandler;
import roart.eureka.util.EurekaUtil;
import roart.service.ControlService;

public class ConvertDAO {

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public ConvertDAO(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public ConvertResult convert(Converter converter, InmemoryMessage message, Map<String, String> metadata, String filename, IndexFiles index) {
        ConvertParam param = new ConvertParam();
        configureParam(param);
        param.message = message;
        param.converter = converter;
        param.filename = filename;
        return EurekaUtil.sendMe(ConvertResult.class, param, converter.getName().toUpperCase(), EurekaConstants.CONVERT, nodeConf);
        //Inmemory inmemory = InmemoryFactory.get(Constants.HAZELCAST, null, null);
        //String newparam = inmemory.read(result.message);
        ////inmemory.delete(message);
        //return newparam;
    }

    private void configureParam(ConvertParam param) {
        param.configname = controlService.getConfigName();
        param.configid = controlService.getConfigId();
        param.iconf = controlService.iconf;
        param.iserver = nodeConf.getInmemoryServer();
        if (Constants.REDIS.equals(nodeConf.getInmemoryServer())) {
            param.iconnection = nodeConf.getInmemoryRedis();
        }
    }

    public MyQueue getQueue(String queueName) {
        String appId = System.getenv(Constants.CONVERTAPPID) != null ? System.getenv(Constants.CONVERTAPPID) : "";
        return new MyQueueFactory().create(queueName + appId, nodeConf, controlService.curatorClient);
    }

    public void convertQueue(QueueElement element, List<Converter> converters, InmemoryMessage message, Map<String, String> metadata, String filename, IndexFiles index) {
        ConvertParam param = new ConvertParam();
        configureParam(param);
        param.message = message;
        param.converters = converters;
        param.filename = filename;
        element.setOpid(OperationConstants.CONVERT);
        element.setConvertParam(param);
        getQueue(converters.get(0).getName()).offer(element);
    }

}
