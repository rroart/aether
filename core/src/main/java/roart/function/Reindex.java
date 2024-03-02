package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.model.IndexFiles;
import roart.common.queue.QueueElement;
import roart.common.service.ServiceParam;
import roart.service.ControlService;
import roart.util.FilterUtil;

public class Reindex extends AbstractIndex {

    public Reindex(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public List doClient(ServiceParam param) {
        return clientDo(param);
    }
    
    @Override
    public boolean indexFilter(IndexFiles index, QueueElement element) {
        return FilterUtil.indexFilterIndex(index, element.getClientQueueElement())
                && FilterUtil.indexFilter(index, element.getClientQueueElement());
    }

}
