package roart.convert;

import java.util.Map;

import roart.common.config.Converter;
import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.eureka.util.EurekaUtil;
import roart.service.ControlService;

public class ConvertDAO {

    public static InmemoryMessage convert(Converter converter, InmemoryMessage message, Map<String, String> metadata, String filename) {
        ConvertParam param = new ConvertParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.message = message;
        param.converter = converter;
        param.filename = filename;
        ConvertResult result = EurekaUtil.sendMe(ConvertResult.class, param, converter.getName().toUpperCase(), EurekaConstants.CONVERT);

        if (result == null || result.message == null) {
            return null;
        }
        if (result.metadata != null) {
            metadata.putAll(result.metadata);
        }
        return result.message;
        //Inmemory inmemory = InmemoryFactory.get(Constants.HAZELCAST, null, null);
        //String newparam = inmemory.read(result.message);
        ////inmemory.delete(message);
        //return newparam;
    }

}
