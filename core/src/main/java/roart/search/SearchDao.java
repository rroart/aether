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
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchDao {
    private static Logger log = LoggerFactory.getLogger(SearchDao.class);

    private static SearchAccess search = null;

    public static void instance(String type) {
        System.out.println("instance " + type);
        log.info("instance " + type);
        if (search != null) {
            // TODO propagate error
            search.destructor();
        }
        if (true || search == null) {
            // TODO make OO of this?
            if (type.equals(ConfigConstants.SEARCHENGINELUCENE)) {
                search = new LuceneSearchAccess();
            }
            if (type.equals(ConfigConstants.SEARCHENGINESOLR)) {
                search = new SolrSearchAccess();
            }
            if (type.equals(ConfigConstants.SEARCHENGINEELASTIC)) {
                search = new ElasticSearchAccess();
            }
            // TODO propagate
            // TODO not
            String error = search.constructor();
        }
    }

    public static int indexme(String type, String md5, FileObject dbfilename, Map<String, String> metadata, String lang, String classification, IndexFiles index, InmemoryMessage message) {
        return search.indexme(type, md5, dbfilename, metadata, lang, classification, index, message);
    }

    public static ResultItem[] searchme(String str, String searchtype) {
        return search.searchme(str, searchtype);
    }

    public static ResultItem[] searchsimilar(String md5i, String searchtype) {
        return search.searchsimilar(md5i, searchtype);
    }

    /*
    public static Query docsLike(int id, IndexReader ind) throws IOException {
    }

    public static Query docsLike(int id, Document doc, IndexReader ind) throws IOException {
    }
     */

    public void deleteme(String str) {
        search.delete(str);
    }

    public static List<String> removeDuplicate() throws Exception {
        return null;
    }

    public static List<String> cleanup2() throws Exception {
        return null;
    }

    public static List<String> removeDuplicate2() throws Exception {
        return null;
    }

    public static void clear() {
        search.clear();
    }

    public static void drop() {
        search.drop();
    }

}
