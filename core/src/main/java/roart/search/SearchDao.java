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
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchResult;

import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchDao {
    private static Logger log = LoggerFactory.getLogger(SearchDao.class);

    private static SearchAccess search = null;

    public static void instance(String type) {
	System.out.println("instance " + type);
	log.info("instance " + type);
	if (search != null) {
		search.destructor();
	}
	if (true || search == null) {
	    if (type.equals(ConfigConstants.SEARCHENGINELUCENE)) {
		search = new LuceneSearchAccess();
	    }
	    if (type.equals(ConfigConstants.SEARCHENGINESOLR)) {
		search = new SolrSearchAccess();
	    }
	    if (type.equals(ConfigConstants.SEARCHENGINEELASTIC)) {
		search = new ElasticSearchAccess();
	    }
	    String error = search.constructor();
	}
    }

    public static int indexme(String type, String md5, InputStream inputStream, String dbfilename, Metadata metadata, String lang, String content, String classification, IndexFiles index) {
	return search.indexme(type, md5, inputStream, dbfilename, metadata, lang, content, classification, index);
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

    public static void deleteme(String str) {
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

}
