package roart.function;

import java.util.List;

import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.queue.TraverseQueueElement;

public abstract class Reindex extends AbstractIndex {

    public Reindex(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        return clientDo(param);
    }
    

}
