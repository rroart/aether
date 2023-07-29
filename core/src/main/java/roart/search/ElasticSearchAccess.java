package roart.search;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.model.SearchDisplay;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.service.ControlService;

import java.util.List;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchAccess extends SearchAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ElasticSearchAccess(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    public String getAppName() {
    	return EurekaConstants.ELASTIC;
    }
    
}

