package roart.function;

import roart.common.config.NodeConfig;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;

public class ReindexDate extends Reindex {

    public ReindexDate(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public boolean indexFilter(IndexFiles index, TraverseQueueElement element) {
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
                return false;
            }
            if (tshigh != null && new Long(timestamp).compareTo(tshigh) <= 0) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

}
