package roart.function;

import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.queue.TraverseQueueElement;

public class ReindexDate extends Reindex {

    public ReindexDate(ServiceParam param) {
        super(param);
    }

    @Override
    public int indexFilter(IndexFiles index, TraverseQueueElement element) {
        String lowerdate = element.getClientQueueElement().lowerdate;
        String higherdate = element.getClientQueueElement().higherdate;
        Long tslow = null;
        if (lowerdate != null) {
            tslow = new Long(lowerdate);
        }
        Long tshigh = null;
        if (higherdate != null) {
            tshigh = new Long(higherdate);
        }

        String timestamp = index.getTimestamp();
        if (timestamp != null) {
            if (tslow != null && new Long(timestamp).compareTo(tslow) >= 0) {
                return 0;
            }
            if (tshigh != null && new Long(timestamp).compareTo(tshigh) <= 0) {
                return 0;
            }
        } else {
            return 0;
        }
        return 1;
    }

}
