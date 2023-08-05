package roart.search;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.constants.QueueConstants;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.model.SearchDisplay;
import roart.service.ControlService;

import java.util.List;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneSearchAccess extends SearchAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public LuceneSearchAccess(NodeConfig nodeConf, ControlService controlService) {
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
