package roart.function;

import java.util.List;

import roart.common.service.ServiceParam;
import roart.database.IndexFilesDao;

public class DbClear extends AbstractFunction {

    public DbClear(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao.clear();
        return null;
    }

}
