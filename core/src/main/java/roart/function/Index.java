package roart.function;

import java.util.List;

import roart.common.constants.Constants;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.queue.TraverseQueueElement;

public class Index extends AbstractIndex {

    public Index(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        return clientDo(param);
    }

    @Override
    public boolean indexFilter(IndexFiles index, TraverseQueueElement element) {
        boolean reindex = element.getClientQueueElement().reindex;
        return reindex == index.getIndexed();
    }

}
