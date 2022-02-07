package roart.function;

import java.util.List;

import roart.common.service.ServiceParam;
import roart.database.IndexFilesDao;

public class DbDrop extends AbstractFunction {

    public DbDrop(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao.drop();
        return null;
    }

}
