package roart.search;

import roart.model.IndexFiles;
import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.lang.LanguageDetect;
import roart.model.ResultItem;
import roart.model.SearchDisplay;

import roart.database.HibernateUtil;


import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
 
import org.apache.tika.metadata.Metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Search {
    private static Logger log = LoggerFactory.getLogger(Search.class);

    //public static int indexme(String type, String md5, InputStream inputStream) {
    public static void indexme() {
    	IndexQueueElement el = Queues.indexQueue.poll();
    	if (el == null) {
    		log.error("empty queue");
    	    return;
    	}
    	// vulnerable spot
    	Queues.incIndexs();
    	long now = System.currentTimeMillis();
    	
    	String type = el.type;
     	String md5 = el.md5;
    	InputStream inputStream = el.inputStream;
    	IndexFiles dbindex = el.index;
    	String dbfilename = el.dbfilename;
	Metadata metadata = el.metadata;
	String lang = el.lang;
	String content = el.content;
	String classification = el.index.getClassification();
    	List<ResultItem> retlist = el.retlist;
    	List<ResultItem> retlistnot = el.retlistnot;

    int retsize = 0;

    try {
    retsize = SearchDao.indexme(type, md5, inputStream, dbfilename, metadata.toString(), lang, content, classification, retlist, dbindex);
	} catch (Exception e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	    dbindex.setNoindexreason(dbindex.getNoindexreason() + "index exception " + e.getClass().getName() + " ");
	    retsize = -1;
	} catch (OutOfMemoryError e) {
	    log.error(roart.util.Constants.ERROR, e);
	    dbindex.setNoindexreason(dbindex.getNoindexreason() + "outofmemory " + e.getClass().getName() + " ");
	    retsize = -1;
    }

    if (retsize < 0) {
	//dbindex.setNoindexreason(Constants.EXCEPTION); // later, propagate the exception
	ResultItem ri = IndexFiles.getResultItem(el.index, "n/a");
	ri.get().set(2, dbfilename);
	retlistnot.add(ri);
    } else {

	log.info("size2 " + retsize);
	el.size = retsize;
	dbindex.setIndexed(Boolean.TRUE);
	dbindex.setTimestamp("" + System.currentTimeMillis());
	dbindex.setConvertsw(el.convertsw);
	//dbindex.save();
	long time = System.currentTimeMillis() - now;
	dbindex.setTimeindex(time);
	log.info("timerStop filename " + time);

	ResultItem ri = IndexFiles.getResultItem(el.index, lang);
	ri.get().set(2, dbfilename);
	retlist.add(ri);
    try {
		inputStream.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		log.error(roart.util.Constants.EXCEPTION, e);
	}
    }
    Queues.decIndexs();
    
	}

    public static ResultItem[] searchme(String str, String searchtype, SearchDisplay display) {
		ResultItem[] strarr = new ResultItem[0];
		
		strarr = SearchDao.searchme(str, searchtype, display);
    return strarr;
}

    // not yet usable, lacking termvector
    public static ResultItem[] searchsimilar(String md5i) {
		ResultItem[] strarr = new ResultItem[0];
    return strarr;
}

    // not yet usable, lacking termvector
    public static void docsLike(int id, int max) throws IOException {
    }

    public static void deleteme(String str) {
    }

    // outdated, did run once, had a bug which made duplicates
    public static List<String> removeDuplicate() throws Exception {
	return null;
    }//End of removeDuplicate method

    // outdated, used once, when bug added filename instead of md5
    public static List<String> cleanup2() throws Exception {
	return null;
    }//End of removeDuplicate method

}
