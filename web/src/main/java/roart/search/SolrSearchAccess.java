package roart.search;

import roart.model.ResultItem;
import roart.model.SearchDisplay;
import roart.util.EurekaConstants;
import scala.collection.parallel.immutable.ParMap;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.config.MyConfig;
import roart.model.IndexFiles;

import java.util.List;

import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrSearchAccess extends SearchAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public String getAppName() {
    	return EurekaConstants.SOLR;
    }

}

