package roart.function;

import java.util.List;

import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.queue.TraverseQueueElement;

public class FilesystemLucenenew extends AbstractIndex {

    public FilesystemLucenenew(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        return clientDo(param);
    }

    @Override
    public int indexFilter(IndexFiles index, TraverseQueueElement element) {
        return 1;
    }

}
