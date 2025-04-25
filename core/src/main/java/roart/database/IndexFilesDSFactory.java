package roart.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;
import roart.service.ControlService;

public class IndexFilesDSFactory {

    private static Logger log = LoggerFactory.getLogger(IndexFilesDSFactory.class);

    public static IndexFilesDS get(NodeConfig nodeConf, ControlService controlService) {
        String type = configDb(nodeConf);
        return get(type, nodeConf, controlService);
    }
    
    public static IndexFilesDS get(String type, NodeConfig nodeConf, ControlService controlService) {
        IndexFilesDS indexFiles = null;
        if (type.equals(ConfigConstants.DATABASEHIBERNATE)) {
            indexFiles  = new HibernateIndexFilesDS(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.DATABASEHBASE)) {
            indexFiles = new HbaseIndexFilesDS(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.DATABASECASSANDRA)) {
            indexFiles = new CassandraIndexFilesDS(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.DATABASEDYNAMODB)) {
            indexFiles = new DynamodbIndexFilesDS(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.DATABASEDATANUCLEUS)) {
            indexFiles = new DataNucleusIndexFilesDS(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.DATABASESPRING)) {
            indexFiles = new SpringDataIndexFilesDS(nodeConf, controlService);
        }
        return indexFiles;
    }

    private static String configDb(NodeConfig nodeConf) {
        String db = null;
        log.debug("type " + nodeConf);
        if (nodeConf.wantHBase()) {
            db = ConfigConstants.DATABASEHBASE;
        } else if (nodeConf.wantCassandra()) {
            db = ConfigConstants.DATABASECASSANDRA;
        } else if (nodeConf.wantDynamodb()) {
            db = ConfigConstants.DATABASEDYNAMODB;
        } else if (nodeConf.wantDataNucleus()) {
            db = ConfigConstants.DATABASEDATANUCLEUS;
        } else if (nodeConf.wantHibernate()) {
            db = ConfigConstants.DATABASEHIBERNATE;
        } else if (nodeConf.wantSpringData()) {
            db = ConfigConstants.DATABASESPRING;
        }
        if (db != null) {
            return db;
        } else {
            log.error("No db selected");
            return null;
        }
    }

}
