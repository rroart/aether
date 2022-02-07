package roart.function;

import java.util.List;

import roart.common.service.ServiceParam;
import roart.search.SearchDao;

public class IndexClean extends AbstractFunction {

    public IndexClean(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        SearchDao.clear();
        return null;
    }

}
