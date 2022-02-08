package roart.function;

import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.queue.TraverseQueueElement;
import roart.util.MyAtomicLong;
import roart.util.MyAtomicLongs;

public abstract class AbstractIndex extends AbstractFunction {

    public AbstractIndex(ServiceParam param) {
        super(param);
    }

    public boolean filterindex(IndexFiles index, TraverseQueueElement trav)
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

        int maxfailed = MyConfig.conf.getFailedLimit();
        if (!trav.getClientQueueElement().reindex && maxfailed > 0 && maxfailed <= index.getFailed().intValue()) {
            return false;
        }

        MyAtomicLong indexcount = MyAtomicLongs.get(Constants.INDEXCOUNT + trav.getMyid()); 

        int indexinc = 0;
        indexinc = indexFilter(index, trav);
        indexcount.addAndGet(indexinc);
        return indexinc > 0;
    }

}