package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;
import roart.database.IndexFilesDS;
import roart.database.IndexFilesDSFactory;
import roart.database.IndexFilesDao;
import roart.service.ControlService;

public class DbDrop extends AbstractFunction {

    public DbDrop(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        String db = param.name;
        if (db == null) {
            indexFilesDao.drop();
        } else {
            IndexFilesDS access = IndexFilesDSFactory.get(db, nodeConf, controlService);
            access.drop();
        }
        return null;
    }

}
