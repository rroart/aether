package roart.function;

import roart.common.collections.impl.MyAtomicLong;
import roart.common.collections.impl.MyAtomicLongs;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.dir.Traverse;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;
import roart.common.queue.QueueElement;

public abstract class AbstractIndex extends AbstractFunction {

    public AbstractIndex(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Deprecated
    public boolean filterindex(IndexFiles index, QueueElement trav)
            throws Exception {
        if (index == null) {
            return false;
        }
        // skip if indexed already, and no reindex wanted
        Boolean indexed = index.getIndexed();
        if (indexed != null) {
            if (!trav.getClientQueueElement().reindex && indexed.booleanValue()) {
                return false;
            }
        }

        String md5 = index.getMd5();

        // if ordinary indexing (no reindexing)
        // and a failed limit it set
        // and the file has come to that limit

        int maxfailed = nodeConf.getFailedLimit();
        if (!trav.getClientQueueElement().reindex && maxfailed > 0 && maxfailed <= index.getFailed().intValue()) {
            return false;
        }

        MyAtomicLong indexcount = MyAtomicLongs.get(Constants.INDEXCOUNT + trav.getMyid(), nodeConf, controlService.curatorClient); 

        boolean indexinc = indexFilter(index, trav);
        if (indexinc) {
            indexcount.addAndGet(1);
        }
        return indexinc;
    }

    @Override
    protected void traverse(String filename, Traverse traverse) {
        try {
            traverse.traversedb(this, filename);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }
}
