package roart.search;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import roart.config.ConfigConstants;
import roart.model.ResultItem;
import roart.model.SearchDisplay;
import roart.model.IndexFiles;

import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchDao {
    private static Logger log = LoggerFactory.getLogger(SearchDao.class);

    private static SearchAccess search = null;

    public static void instance(String type) {
	System.out.println("instance " + type);
	log.info("instance " + type);
	if (search == null) {
	    if (type.equals(ConfigConstants.LUCENE)) {
		search = new LuceneSearchAccess();
	    }
	    if (type.equals(ConfigConstants.SOLR)) {
		search = new SolrSearchAccess();
	    }
	    if (type.equals(ConfigConstants.ELASTIC)) {
		search = new ElasticSearchAccess();
	    }
	}
    }

    public static int indexme(String type, String md5, InputStream inputStream, String dbfilename, Metadata metadata, String lang, String content, String classification, IndexFiles index) {
	return search.indexme(type, md5, inputStream, dbfilename, metadata, lang, content, classification, index);
    }

    public static ResultItem[] searchme(String str, String searchtype, SearchDisplay display) {
	return search.searchme(str, searchtype, display);
    }

    public static ResultItem[] searchsimilar(String md5i, String searchtype, SearchDisplay display) {
	return search.searchsimilar(md5i, searchtype, display);
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