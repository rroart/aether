package roart.service;

import javax.servlet.http.*;

import java.util.Vector;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.model.ResultItem;
import roart.common.model.SearchDisplay;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.service.ServiceParam.Function;
import roart.eureka.util.EurekaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.UI;

public class SearchService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public List searchme(String str, String type) {
        SearchEngineSearchParam param = new SearchEngineSearchParam();
        param.conf = getConfig();
        param.str = str;
        param.searchtype = type;
        SearchEngineSearchResult result = EurekaUtil.sendMe(SearchEngineSearchResult.class, param, getAppName(), EurekaConstants.SEARCH, nodeConf);
        List lists = result.list;        
        return lists;           
    }

    // TODO fix
	public static SearchDisplay getSearchDisplay(UI ui) {
	    SearchDisplay d = new SearchDisplay();
	    d.highlightmlt = true;
	    d.classify = true;
	    d.admindisplay = true;
	    return d;
	}

	public static boolean isHighlightMLT() {
		return MyConfig.conf.getHighlightmlt();
	}
    
    public void searchsimilar(String md5) {
        SearchEngineSearchParam param = new SearchEngineSearchParam();
        param.conf = getConfig();
        param.str = md5;
        SearchEngineSearchResult result = EurekaUtil.sendMe(SearchEngineSearchResult.class, param, getAppName(), EurekaConstants.SEARCHMLT, nodeConf);
        return;           
    }

    private String getAppName() {
	return EurekaConstants.AETHER;
    }
    
    private NodeConfig getConfig() {
        return MyConfig.conf;
    }


}
