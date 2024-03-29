package roart.search;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.queue.QueueElement;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.service.ControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchDao {
    private static Logger log = LoggerFactory.getLogger(SearchDao.class);

    private SearchAccess search = null;

    private NodeConfig nodeConf;
    
    public SearchDao(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.search = SearchAccessFactory.get(nodeConf, controlService);
    }

    public int indexme(String md5, FileObject dbfilename, Map<String, String> metadata, String lang, String classification, IndexFiles index, InmemoryMessage message) {
        return search.indexme(md5, dbfilename, metadata, lang, classification, index, message);
    }

    public ResultItem[] searchme(String str, String searchtype) {
        return search.searchme(str, searchtype);
    }

    public ResultItem[] searchsimilar(String md5i, String searchtype) {
        return search.searchsimilar(md5i, searchtype);
    }

    /*
    public Query docsLike(int id, IndexReader ind) throws IOException {
    }

    public Query docsLike(int id, Document doc, IndexReader ind) throws IOException {
    }
     */

    public void deleteme(String str) {
        search.delete(str);
    }

    public List<String> removeDuplicate() throws Exception {
        return null;
    }

    public List<String> cleanup2() throws Exception {
        return null;
    }

    public List<String> removeDuplicate2() throws Exception {
        return null;
    }

    public void clear() {
        search.clear();
    }

    public void drop() {
        search.drop();
    }

    public void indexmeQueue(QueueElement element, String md5, FileObject dbfilename, Map<String, String> metadata, String lang, String classification, IndexFiles index, InmemoryMessage message) {
        search.indexmeQueue(element, md5, dbfilename, metadata, lang, classification, index, message);
    }

}
