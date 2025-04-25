package roart.search;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.service.ControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneSearchDS extends SearchDS {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public LuceneSearchDS(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    public String getAppName() {
    	return EurekaConstants.LUCENE;
    }
    
    @Override
    public String getQueueName() {
        return QueueConstants.LUCENE;
    }

}
