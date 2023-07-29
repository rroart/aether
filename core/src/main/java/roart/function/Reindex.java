package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;

public abstract class Reindex extends AbstractIndex {

    public Reindex(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public List doClient(ServiceParam param) {
        return clientDo(param);
    }
    

}
