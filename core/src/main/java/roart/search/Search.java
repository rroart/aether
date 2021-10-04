package roart.search;

import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.service.ControlService;
import roart.service.SearchService;
import roart.util.MyList;
import roart.util.MyLists;
import roart.lang.LanguageDetect;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.searchengine.SearchResult;
import roart.common.util.JsonUtil;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.inmemory.model.Inmemory;
import roart.common.constants.Constants;
import roart.common.inmemory.factory.InmemoryFactory;

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
    	//InputStream inputStream = el.inputStream;
    	IndexFiles dbindex = el.index;
    	String dbfilename = el.dbfilename;
	Metadata metadata = el.metadata;
	String lang = el.lang;
	String content = getParam(el.message);
	String classification = el.index.getClassification();
    	MyList<ResultItem> retlist = MyLists.get(el.retlistid);
    	MyList<ResultItem> retlistnot = MyLists.get(el.retlistnotid);

    	int retsize = 0;

    try {
    retsize = SearchDao.indexme(type, md5, dbfilename, metadata, lang, content, classification, dbindex);
    } catch (Exception e) {
	    log.error(roart.common.constants.Constants.EXCEPTION, e);
	    dbindex.setNoindexreason(dbindex.getNoindexreason() + "index exception " + e.getClass().getName() + " ");
	    retsize = -1;
	} catch (OutOfMemoryError e) {
	    System.gc();
	    log.error("Error " + Thread.currentThread().getId() + " " + dbfilename);
	    log.error(roart.common.constants.Constants.ERROR, e);
	    dbindex.setNoindexreason(dbindex.getNoindexreason() + "outofmemory " + e.getClass().getName() + " ");
	    retsize = -1;
    }

    if (retsize < 0) {
	//dbindex.setNoindexreason(Constants.EXCEPTION); // later, propagate the exception
        FileLocation maybeFl = Traverse.getExistingLocalFilelocationMaybe(el.index);
	ResultItem ri = IndexFiles.getResultItem(el.index, el.index.getLanguage(), ControlService.nodename, maybeFl);
	ri.get().set(IndexFiles.FILENAMECOLUMN, dbfilename);
	retlistnot.add(ri);
    } else {

	log.info("size2 " + md5 + " " + retsize);
	el.size = retsize;
	dbindex.setIndexed(Boolean.TRUE);
	dbindex.setTimestamp("" + System.currentTimeMillis());
	dbindex.setConvertsw(el.convertsw);
	//dbindex.save();
	long time = System.currentTimeMillis() - now;
	dbindex.setTimeindex("" + time);
	log.info("timerStop filename " + time);

    FileLocation maybeFl = Traverse.getExistingLocalFilelocationMaybe(el.index);
	ResultItem ri = IndexFiles.getResultItem(el.index, lang, ControlService.nodename, maybeFl);
	ri.get().set(IndexFiles.FILENAMECOLUMN, dbfilename);
	retlist.add(ri);
	
    }
    dbindex.setPriority(1);
    // file unlock dbindex
    // config with finegrained distrib
    IndexFilesDao.add(dbindex);
    Queues.decIndexs();
    
    if (el.message != null) {
        Inmemory inmemory = InmemoryFactory.get(Constants.HAZELCAST, null, null);
        inmemory.delete(el.message);
    }
	}

    public static ResultItem[] searchme(String str, String searchtype) {
    	return SearchDao.searchme(str, searchtype);
}

    // not yet usable, lacking termvector
    public static ResultItem[] searchsimilar(String md5i, String searchtype) {
		return SearchDao.searchsimilar(md5i, searchtype);
}

    // not yet usable, lacking termvector
    public static void docsLike(int id, int max) throws IOException {
    }

    public static void deleteme(String str) {
		SearchDao.deleteme(str);
    }

    // outdated, did run once, had a bug which made duplicates
    public static List<String> removeDuplicate() throws Exception {
	return null;
    }//End of removeDuplicate method

    // outdated, used once, when bug added filename instead of md5
    public static List<String> cleanup2() throws Exception {
	return null;
    }//End of removeDuplicate method

    private static String getParam(InmemoryMessage message) {
        //InmemoryMessage message = JsonUtil.convert(message, InmemoryMessage.class);
        //Inmemory inmemory = InmemoryFactory.get(instance.getInmemoryServer(), instance.getInmemoryHazelcast(), instance.getInmemoryRedis());
        Inmemory inmemory = InmemoryFactory.get(Constants.HAZELCAST, null, null);
       return inmemory.read(message);
    }

}
