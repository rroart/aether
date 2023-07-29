package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;
import roart.service.ControlService;

public class SearchSimilar extends AbstractFunction {

    public SearchSimilar(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public List doClient(ServiceParam param) {
        return null;
    }

}
