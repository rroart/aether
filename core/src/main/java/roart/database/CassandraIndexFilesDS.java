package roart.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.service.ControlService;

public class CassandraIndexFilesDS extends IndexFilesDS {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CassandraIndexFilesDS(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    public String getAppName() {
	return EurekaConstants.CASSANDRA;
    }

    public String getQueueName() {
        return QueueConstants.CASSANDRA;
    }

}
