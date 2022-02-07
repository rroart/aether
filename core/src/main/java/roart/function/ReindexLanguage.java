package roart.function;

import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.queue.TraverseQueueElement;

public class ReindexLanguage extends Reindex {

    public ReindexLanguage(ServiceParam param) {
        super(param);
    }

    @Override
    public int indexFilter(IndexFiles index, TraverseQueueElement element) {
        String mylanguage = index.getLanguage();
        if (mylanguage != null && mylanguage.equals(element.getClientQueueElement().suffix)) { // stupid overload
            return 1;
        }
        return 0;

    }

}
