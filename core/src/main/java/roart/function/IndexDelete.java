package roart.function;

import java.util.List;

import roart.common.service.ServiceParam;
import roart.search.SearchDao;

public class IndexDelete extends AbstractFunction {

    public IndexDelete(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        SearchDao.drop();
        return null;
    }

}
