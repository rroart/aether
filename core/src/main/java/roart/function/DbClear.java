package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;
import roart.database.IndexFilesAccess;
import roart.database.IndexFilesAccessFactory;
import roart.database.IndexFilesDao;

public class DbClear extends AbstractFunction {

    public DbClear(ServiceParam param, NodeConfig nodeConf) {
        super(param, nodeConf);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf);
        String db = param.name;
        if (db == null) {
            indexFilesDao.clear();
        } else {
            IndexFilesAccess access = IndexFilesAccessFactory.get(db, nodeConf);
            access.clear();
        }
        return null;
    }

}
