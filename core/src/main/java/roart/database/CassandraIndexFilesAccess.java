package roart.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class CassandraIndexFilesAccess extends IndexFilesAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CassandraIndexFilesAccess(NodeConfig nodeConf) {
        super(nodeConf);
    }

    public String getAppName() {
	return EurekaConstants.CASSANDRA;
    }

}
