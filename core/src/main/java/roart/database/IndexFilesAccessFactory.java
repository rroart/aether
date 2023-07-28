package roart.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;

public class IndexFilesAccessFactory {

    private static Logger log = LoggerFactory.getLogger(IndexFilesAccessFactory.class);

    public static IndexFilesAccess get(NodeConfig nodeConf) {
        String type = configDb(nodeConf);
        return get(type, nodeConf);
    }
    
    public static IndexFilesAccess get(String type, NodeConfig nodeConf) {
        IndexFilesAccess indexFiles = null;
        if (type.equals(ConfigConstants.DATABASEHIBERNATE)) {
            indexFiles  = new HibernateIndexFilesAccess(nodeConf);
        }
        if (type.equals(ConfigConstants.DATABASEHBASE)) {
            indexFiles = new HbaseIndexFilesAccess(nodeConf);
        }
        if (type.equals(ConfigConstants.DATABASECASSANDRA)) {
            indexFiles = new CassandraIndexFilesAccess(nodeConf);
        }
        if (type.equals(ConfigConstants.DATABASEDYNAMODB)) {
            indexFiles = new DynamodbIndexFilesAccess(nodeConf);
        }
        if (type.equals(ConfigConstants.DATABASEDATANUCLEUS)) {
            indexFiles = new DataNucleusIndexFilesAccess(nodeConf);
        }
        if (type.equals(ConfigConstants.DATABASESPRING)) {
            indexFiles = new SpringDataIndexFilesAccess(nodeConf);
        }
        return indexFiles;
    }

    private static String configDb(NodeConfig nodeConf) {
        String db = null;
        System.out.println("type " + nodeConf);
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
