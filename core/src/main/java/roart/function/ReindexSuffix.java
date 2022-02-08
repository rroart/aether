package roart.function;

import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.common.util.FsUtil;
import roart.filesystem.FileSystemDao;
import roart.queue.TraverseQueueElement;

public class ReindexSuffix extends Reindex {

    public ReindexSuffix(ServiceParam param) {
        super(param);
    }

    @Override
    public int indexFilter(IndexFiles index, TraverseQueueElement element) {
        for (FileLocation fl : index.getFilelocations()) {
            if (element.getClientQueueElement().suffix != null && !fl.getFilename().endsWith(element.getClientQueueElement().suffix)) {
                continue;
            }
            return 1;
        }
        return 0;
    }

}