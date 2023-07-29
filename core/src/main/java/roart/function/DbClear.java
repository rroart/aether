package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;
import roart.database.IndexFilesAccess;
import roart.database.IndexFilesAccessFactory;
import roart.database.IndexFilesDao;
import roart.service.ControlService;

public class DbClear extends AbstractFunction {

    public DbClear(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        String db = param.name;
        if (db == null) {
            indexFilesDao.clear();
        } else {
            IndexFilesAccess access = IndexFilesAccessFactory.get(db, nodeConf, controlService);
            access.clear();
        }
        return null;
    }

}
