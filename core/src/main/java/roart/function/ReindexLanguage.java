package roart.function;

import roart.common.config.NodeConfig;
import roart.common.model.IndexFiles;
import roart.common.queue.QueueElement;
import roart.common.service.ServiceParam;
import roart.service.ControlService;

public class ReindexLanguage extends Reindex {

    public ReindexLanguage(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public boolean indexFilter(IndexFiles index, QueueElement element) {
        String mylanguage = index.getLanguage();
        if (mylanguage != null && mylanguage.equals(element.getClientQueueElement().suffix)) { // stupid overload
            return true;
        }
        return false;

    }

}
