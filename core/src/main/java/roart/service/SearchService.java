package roart.service;

import roart.queue.Queues;
import roart.search.SearchDao;
import roart.service.ServiceParam.Function;

import javax.servlet.http.*;

import java.util.Vector;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import roart.common.searchengine.SearchEngineSearchParam;
import roart.config.ConfigConstants;
import roart.config.MyConfig;
//import roart.dao.FilesDao;
import roart.database.IndexFilesDao;
import roart.model.ResultItem;
import roart.model.SearchDisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public List searchme(SearchEngineSearchParam e) {
        return searchmeDo(e);
    }

    public List<List> searchmeDo(SearchEngineSearchParam e) {
	String str = e.str;
	String type = e.searchtype;
	List strlist = new ArrayList<String>();

	ResultItem[] strarr = roart.search.Search.searchme(str, type);
	
	for (ResultItem stri : strarr) {
	    strlist.add(stri);
	}
	List<List> strlistlist = new ArrayList<List>();
	strlistlist.add(strlist);
	return strlistlist;
    }

    public List<List> searchsimilarDo(SearchEngineSearchParam e) {
	String str = e.str;
	String type = e.searchtype;
	List strlist = new ArrayList<String>();

	ResultItem[] strarr = roart.search.Search.searchsimilar(str, type);

	for (ResultItem stri : strarr) {
	    strlist.add(stri);
	}
	List<List> strlistlist = new ArrayList<List>();
	strlistlist.add(strlist);
	return strlistlist;
    }

	public static boolean isHighlightMLT() {
		
		return MyConfig.conf.getHighlightmlt();
	}
    
    public List searchsimilar(SearchEngineSearchParam e) {
            return searchsimilarDo(e);
	    }
}
