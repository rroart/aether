package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.dir.Traverse;
import roart.queue.TraverseQueueElement;

public class FilesystemLucenenew extends AbstractIndex {

    public FilesystemLucenenew(ServiceParam param, NodeConfig nodeConf) {
        super(param, nodeConf);
    }

    @Override
    public List doClient(ServiceParam param) {
        return clientDo(param);
    }

    @Override
    public boolean indexFilter(IndexFiles index, TraverseQueueElement element) {
        return true;
    }

    @Override
    protected void traverse(String filename, Traverse traverse) {
        try {
            traverse.traverse(filename, this);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }
}
