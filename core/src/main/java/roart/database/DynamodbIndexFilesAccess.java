package roart.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class DynamodbIndexFilesAccess extends IndexFilesAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public DynamodbIndexFilesAccess(NodeConfig nodeConf) {
        super(nodeConf);
        // TODO Auto-generated constructor stub
    }

    public String getAppName() {
	return EurekaConstants.DYNAMODB;
    }

}
