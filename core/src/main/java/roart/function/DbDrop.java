package roart.function;

import java.util.List;

import roart.common.service.ServiceParam;
import roart.database.IndexFilesAccess;
import roart.database.IndexFilesAccessFactory;
import roart.database.IndexFilesDao;

public class DbDrop extends AbstractFunction {

    public DbDrop(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao();
        String db = param.name;
        if (db == null) {
            indexFilesDao.drop();
        } else {
            IndexFilesAccess access = IndexFilesAccessFactory.get(db);
            access.drop();
        }
        return null;
    }

}
