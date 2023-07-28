package roart.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;

public class SpringDataIndexFilesAccess extends IndexFilesAccess {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public SpringDataIndexFilesAccess(NodeConfig nodeConf) {
        super(nodeConf);
    }

    public String getAppName() {
        return EurekaConstants.SPRINGDATA;
    }

}
