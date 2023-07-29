package roart.service;

import roart.queue.Queues;
import roart.search.SearchDao;

import java.util.Vector;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.model.ResultItem;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.database.IndexFilesDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public SearchService(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
  }

    public List searchme(SearchEngineSearchParam e) {
        return searchmeDo(e);
    }

    public List<List> searchmeDo(SearchEngineSearchParam e) {
        String str = e.str;
        String type = e.searchtype;
        List strlist = new ArrayList<String>();

        ResultItem[] strarr = new roart.search.Search(nodeConf, controlService).searchme(str, type);

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

        ResultItem[] strarr = new roart.search.Search(nodeConf, controlService).searchsimilar(str, type);

        for (ResultItem stri : strarr) {
            strlist.add(stri);
        }
        List<List> strlistlist = new ArrayList<List>();
        strlistlist.add(strlist);
        return strlistlist;
    }

    public boolean isHighlightMLT() {

        return nodeConf.getHighlightmlt();
    }

    public List searchsimilar(SearchEngineSearchParam e) {
        return searchsimilarDo(e);
    }
}
