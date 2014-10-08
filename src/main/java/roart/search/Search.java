package roart.search;

import roart.model.IndexFiles;
import roart.model.HibernateUtil;
import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.lang.LanguageDetect;
import roart.model.ResultItem;
import roart.model.SearchDisplay;

import roart.dao.SearchDao;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
 
import org.apache.tika.metadata.Metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Search {
    private static Log log = LogFactory.getLog("Search");

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

    retsize = SearchDao.indexme(type, md5, inputStream, dbfilename, metadata.toString(), lang, content, classification, retlist);

    if (retsize < 0) {
	dbindex.setNoindexreason("Exception"); // later, propagate the exception
	String myclassify = roart.util.Prop.getProp().getProperty("myclassify");
	boolean doclassify = myclassify != null && myclassify.length() > 0;
	ResultItem ri = new ResultItem();
	ri.add("too small");
	ri.add(md5);
	ri.add(dbfilename);
	ri.add("lang");
	if (doclassify) {
	    ri.add(el.index.getClassification());
	}
	ri.add(el.index.getTimestampDate().toString());
	ri.add(el.index.getConvertsw());
	ri.add(el.index.getConverttime("%.2f"));
	ri.add(el.index.getTimeindex("%.2f"));
	if (doclassify) {
	    ri.add(el.index.getTimeclass("%.2f"));
	}
	ri.add("" + el.index.getFailed());
	ri.add(el.index.getFailedreason());
	ri.add(el.index.getTimeoutreason());
	ri.add(el.index.getNoindexreason());
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

	String myclassify = roart.util.Prop.getProp().getProperty("myclassify");
	boolean doclassify = myclassify != null && myclassify.length() > 0;

	ResultItem ri = new ResultItem();
	ri.add("true");
	ri.add(md5);
	ri.add(dbfilename);
	ri.add(lang);
	if (doclassify) {
	    ri.add(el.index.getClassification());
	}
	ri.add(el.index.getTimestamp().toString());
	ri.add(el.index.getConvertsw());
	ri.add(el.index.getConverttime("%.2f"));
	ri.add(el.index.getTimeindex("%.2f"));
	if (doclassify) {
	    ri.add(el.index.getTimeclass("%.2f"));
	}
	ri.add("" + el.index.getFailed());
	ri.add(el.index.getFailedreason());
	ri.add(el.index.getTimeoutreason());
	ri.add(el.index.getNoindexreason());
	retlist.add(ri);
    try {
		inputStream.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		log.error("Exception", e);
	}
    }
    Queues.decIndexs();
    
	}

    public static void indexme(String type) {
	SearchDao.indexme(type);
    }

    public static ResultItem[] searchme(String type, String str) {
		ResultItem[] strarr = new ResultItem[0];
		strarr = SearchDao.searchme(type, str);
    return strarr;
}

    public static ResultItem[] searchme2(String str, String searchtype, SearchDisplay display) {
	String type = "all";
	int stype = new Integer(searchtype).intValue();
		ResultItem[] strarr = new ResultItem[0];
		
		strarr = SearchDao.searchme2(str, searchtype, display);
    return strarr;
}

    // not yet usable, lacking termvector
    public static ResultItem[] searchsimilar(String md5i) {
	String type = "all";
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
