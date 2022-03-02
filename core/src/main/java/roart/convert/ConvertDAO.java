package roart.convert;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import roart.common.config.Connector;
import roart.common.config.Converter;
import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.util.JsonUtil;
import roart.common.webflux.WebFluxUtil;
import roart.eureka.util.EurekaUtil;
import roart.service.ControlService;

public class ConvertDAO {

    public static InmemoryMessage convert(Converter converter, InmemoryMessage message, Map<String, String> metadata, String filename) {
        String connectorString = MyConfig.conf.getConnectors();
        Connector[] connectors = JsonUtil.convert(connectorString, Connector[].class);
        Map<String, Connector> connectMap = Arrays.asList(connectors).stream().collect(Collectors.toMap(Connector::getName, Function.identity()));
        Connector connector = connectMap.get(converter.getName());
        ConvertParam param = new ConvertParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.message = message;
        param.converter = converter;
        param.filename = filename;
        ConvertResult result;
        if (connector.isEureka()) {
            result = EurekaUtil.sendMe(ConvertResult.class, param, converter.getName().toUpperCase(), EurekaConstants.CONVERT);
        } else {
            String url = connector.getConnection();
            result = WebFluxUtil.sendMe(ConvertResult.class, url, param, EurekaConstants.CONVERT);            
        }
        if (result == null) {
            return null;
        }
        // get md from Tika and use it, even if Tika fails
        if (result.metadata != null) {
            metadata.putAll(result.metadata);
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

}
