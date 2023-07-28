package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;
import roart.search.SearchDao;

public class IndexClean extends AbstractFunction {

    public IndexClean(ServiceParam param, NodeConfig nodeConf) {
        super(param, nodeConf);
    }

    @Override
    public List doClient(ServiceParam param) {
        new SearchDao(nodeConf).clear();
        return null;
    }

}
