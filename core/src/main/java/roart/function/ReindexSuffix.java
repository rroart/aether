package roart.function;

import roart.common.config.NodeConfig;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.common.util.FsUtil;
import roart.filesystem.FileSystemDao;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;

public class ReindexSuffix extends Reindex {

    public ReindexSuffix(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public boolean indexFilter(IndexFiles index, TraverseQueueElement element) {
        for (FileLocation fl : index.getFilelocations()) {
            if (element.getClientQueueElement().suffix != null && !fl.getFilename().endsWith(element.getClientQueueElement().suffix)) {
                continue;
            }
            return true;
        }
        return false;
    }

}
