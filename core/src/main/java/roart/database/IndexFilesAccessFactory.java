package roart.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;
import roart.service.ControlService;

public class IndexFilesAccessFactory {

    private static Logger log = LoggerFactory.getLogger(IndexFilesAccessFactory.class);

    public static IndexFilesAccess get(NodeConfig nodeConf, ControlService controlService) {
        String type = configDb(nodeConf);
        return get(type, nodeConf, controlService);
    }
    
    public static IndexFilesAccess get(String type, NodeConfig nodeConf, ControlService controlService) {
        IndexFilesAccess indexFiles = null;
        if (type.equals(ConfigConstants.DATABASEHIBERNATE)) {
            indexFiles  = new HibernateIndexFilesAccess(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.DATABASEHBASE)) {
            indexFiles = new HbaseIndexFilesAccess(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.DATABASECASSANDRA)) {
            indexFiles = new CassandraIndexFilesAccess(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.DATABASEDYNAMODB)) {
            indexFiles = new DynamodbIndexFilesAccess(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.DATABASEDATANUCLEUS)) {
            indexFiles = new DataNucleusIndexFilesAccess(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.DATABASESPRING)) {
            indexFiles = new SpringDataIndexFilesAccess(nodeConf, controlService);
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
