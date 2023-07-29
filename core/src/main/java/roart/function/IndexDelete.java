package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;
import roart.search.SearchDao;
import roart.service.ControlService;

public class IndexDelete extends AbstractFunction {

    public IndexDelete(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public List doClient(ServiceParam param) {
        new SearchDao(nodeConf, controlService).drop();
        return null;
    }

}
