package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;
import roart.database.IndexFilesAccess;
import roart.database.IndexFilesAccessFactory;
import roart.database.IndexFilesDao;

public class DbDrop extends AbstractFunction {

    public DbDrop(ServiceParam param, NodeConfig nodeConf) {
        super(param, nodeConf);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf);
        String db = param.name;
        if (db == null) {
            indexFilesDao.drop();
        } else {
            IndexFilesAccess access = IndexFilesAccessFactory.get(db, nodeConf);
            access.drop();
        }
        return null;
    }

}
