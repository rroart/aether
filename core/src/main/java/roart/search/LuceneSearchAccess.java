package roart.search;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.model.SearchDisplay;

import java.util.List;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneSearchAccess extends SearchAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public LuceneSearchAccess(NodeConfig nodeConf) {
        super(nodeConf);
    }

    public String getAppName() {
    	return EurekaConstants.LUCENE;
    }
    
}
