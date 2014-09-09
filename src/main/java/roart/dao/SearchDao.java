package roart.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import roart.jpa.SearchJpa;
import roart.jpa.LuceneSearchJpa;
import roart.jpa.SolrSearchJpa;
 
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SearchDao {
    private static Log log = LogFactory.getLog("SearchDao");

    private static SearchJpa searchJpa = null;

    public static void instance(String type) {
	System.out.println("instance " + type);
	log.info("instance " + type);
	if (searchJpa == null) {
	    if (type.equals("lucene")) {
		searchJpa = new LuceneSearchJpa();
	    }
	    if (type.equals("solr")) {
		searchJpa = new SolrSearchJpa();
	    }
	}
    }

    public static int indexme(String type, String md5, InputStream inputStream, String dbfilename, String metadata, List<String> retlist) {
	return searchJpa.indexme(type, md5, inputStream, dbfilename, metadata, retlist);
    }

    public static void indexme(String type) {
	searchJpa.indexme(type);
    }

    public static String [] searchme(String type, String str) {
	return searchJpa.searchme(type, str);
    }

    public static String [] searchme2(String str, String searchtype) {
	String type = "all";
	int stype = new Integer(searchtype).intValue();
	return searchJpa.searchme2(str, searchtype);
    }

    public static String [] searchsimilar(String md5i) {
	return null;
    }

    /*
    public static Query docsLike(int id, IndexReader ind) throws IOException {
    }

    public static Query docsLike(int id, Document doc, IndexReader ind) throws IOException {
    }
    */

    public static void deleteme(String str) {
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
