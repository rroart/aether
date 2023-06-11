package roart.database;

import roart.common.config.ConfigConstants;

public class IndexFilesAccessFactory {

    public static IndexFilesAccess get(String type) {
        IndexFilesAccess indexFiles = null;
        if (type.equals(ConfigConstants.DATABASEHIBERNATE)) {
            indexFiles  = new HibernateIndexFilesAccess();
        }
        if (type.equals(ConfigConstants.DATABASEHBASE)) {
            indexFiles = new HbaseIndexFilesAccess();
        }
        if (type.equals(ConfigConstants.DATABASECASSANDRA)) {
            indexFiles = new CassandraIndexFilesAccess();
        }
        if (type.equals(ConfigConstants.DATABASEDYNAMODB)) {
            indexFiles = new DynamodbIndexFilesAccess();
        }
        if (type.equals(ConfigConstants.DATABASEDATANUCLEUS)) {
            indexFiles = new DataNucleusIndexFilesAccess();
        }
        if (type.equals(ConfigConstants.DATABASESPRING)) {
            indexFiles = new SpringDataIndexFilesAccess();
        }
        return indexFiles;
    }

}
