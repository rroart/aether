package roart.dir;

import java.util.Map;
import java.io.*;
import roart.content.*;
import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.search.*;
import roart.thread.TikaRunner;
import roart.model.*;
import roart.lang.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import roart.util.ExecCommand;

public class Traverse {

    private static int MAXFILE = 500;

    private static Log log = LogFactory.getLog("Traverse");

    public static Set<String> doList (String dirname, Map<String, HashSet<String>> dirset) throws Exception {
	Set<String> retset = new HashSet<String>();
	HashSet<String> md5set = new HashSet<String>();
	File dir = new File(dirname);
	File listDir[] = dir.listFiles();
	//log.info("dir " + dirname);
	//log.info("listDir " + listDir.length);
	for (int i = 0; i < listDir.length; i++) {
	    String filename = listDir[i].getAbsolutePath();
	    if (filename.length() > MAXFILE) {
		log.info("Too large filesize " + filename);
		continue;
	    }
	    //log.info("file " + filename);
	    if (listDir[i].isDirectory()) {
		//log.info("isdir " + filename);
		retset.addAll(doList(filename, dirset));
	    } else {
		//log.info("retset " + filename);
		retset.add(filename);
		//Reader reader = new ParsingReader(parser, stream, ...);
		Files files = Files.ensureExistence(filename);
		//files.setTouched(Boolean.TRUE);
		if (files.getMd5() == null) {
		    try {
			FileInputStream fis = new FileInputStream( new File(filename));
			String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex( fis );
			files.setMd5(md5);
			log.info("adding md5 file " + filename);
		    } catch (Exception e) {
			log.info("Error: " + e.getMessage());
			log.error("Exception", e);
		    }
		}
		md5set.add(files.getMd5());
	    }
	}
	dirset.put(dirname, md5set);
	//log.info("retsize " + retset.size());
	return retset;
    }

    public static Set<String> dupdir (String dirname) throws Exception {
	boolean onlyone = false;
	boolean error = false;
	int count = 0;
	long size = 0;
	Set<String> retset = new HashSet<String>();
	HashSet<String> md5set = new HashSet<String>();
	File dir = new File(dirname);
	File listDir[] = dir.listFiles();
	for (int i = 0; i < listDir.length; i++) {
	    String filename = listDir[i].getAbsolutePath();
	    if (filename.length() > MAXFILE) {
		log.info("Too large filesize " + filename);
		error = true;
		continue;
	    }
	    if (listDir[i].isDirectory()) {
		retset.addAll(dupdir(filename));
	    } else {
		if (error) {
		    continue;
		}
		Files files = Files.getByFilename(filename);
		if (files == null) {
		    error = true;
		    continue;
		}
		String md5 = files.getMd5();
		if (md5 == null) {
		    error = true;
		    continue;
		}
		if (Files.getByMd5(md5).size() < 2) {
		    onlyone = true;
		}
		count++;
		size+=new File(filename).length();
	    }
	}
	if (!error && !onlyone && count>0) {
	    retset.add(dirname + " size " + size);
	}
	return retset;
    }

    public static List<String> index() throws Exception {
	List<String> retlist = new ArrayList<String>();
	log.info("here");
	List<Files> files = Files.getAll();
	List<Index> indexes = Index.getAll();
	log.info("sizes " + files.size() + " " + indexes.size());
	Map<String, String> filesMapMd5 = new HashMap<String, String>();
	Map<String, String> filesMapFilename = new HashMap<String, String>();
	for (Files file : files) {
	    String filename = file.getFilename();
	    String md5 = file.getMd5();
	    if (md5 == null) {
		retlist.add("No md5 " + filename);
		continue;
	    }
	    filesMapMd5.put(md5, filename);
	    filesMapFilename.put(filename, md5);
	}
	Map<String, Boolean> indexMap = new HashMap<String, Boolean>();
	for (Index index : indexes) {
	    indexMap.put(index.getMd5(), index.getIndexed());
	}
	for (String md5 : filesMapMd5.keySet()) {
	    indexsingle(retlist, md5, indexMap, filesMapMd5, false);
	}
	return retlist;
    }

    public static List<String> index(String add, boolean reindex) throws Exception {
	List<String> retlist = new ArrayList<String>();
	Set<String> md5set = new HashSet<String>();
	String dirname = add;
	File dir = new File(dirname);
	if (!dir.exists()) {
	    retlist.add("File " + add + " does not exist");
	    return retlist;
	}
	File listDir[];
	if (dir.isDirectory()) {
	    listDir = dir.listFiles();
	} else {
	    listDir = new File[1];
	    listDir[0] = dir;
	}
	//log.info("dir " + dirname);
	log.info("listDir " + listDir.length);
	for (int i = 0; i < listDir.length; i++) {
	    String filename = listDir[i].getAbsolutePath();
	    log.info("file " + filename);
	    if (listDir[i].isDirectory()) {
		//log.info("isdir " + filename);
		retlist.addAll(index(filename, reindex));
	    } else {
		//log.info("retset " + filename);
		//Reader reader = new ParsingReader(parser, stream, ...);
		Files files = Files.getByFilename(filename);
		//files.setTouched(Boolean.TRUE);
		if (files == null || files.getMd5() == null) {
		    continue;
		}
		retlist.add(filename);
		String md5 = files.getMd5();
		if (md5 == null) {
		    continue;
		}
		Index index = Index.getByMd5(md5);

		Map<String, String> filesMapMd5 = new HashMap<String, String>();
		filesMapMd5.put(files.getMd5(), files.getFilename());

		Map<String, Boolean> indexMap = new HashMap<String, Boolean>();
		if (index != null) {
		    indexMap.put(index.getMd5(), index.getIndexed());
		}

		indexsingle(retlist, md5, indexMap, filesMapMd5, reindex);
	    }
	    log.info("file " + filename);
	}
	return retlist;
    }

    public static void indexsingle(List<String> retlist, String md5, Map<String, Boolean> indexMap, Map<String, String> filesMapMd5, boolean reindex) throws Exception {
	    if (md5 == null) {
		log.error("md5 should not be null");
		return;
	    }
	    Boolean indexed = indexMap.get(md5);
	    if (indexed != null) {
		if (!reindex && indexed.booleanValue()) {
		    return;
		}
	    }
	    
	    String filename = filesMapMd5.get(md5);
	    Index index = Index.ensureExistence(md5);
	    //InputStream stream = null;
	    int size = 0;
	    TikaQueueElement e = new TikaQueueElement(filename, filename, md5, index, retlist);
	    Queues.tikaQueue.add(e);
	    //size = doTika(filename, filename, md5, index, retlist);
    }

    public static List<String> notindexed() throws Exception {
	List<String> retlist = new ArrayList<String>();
	List<Files> files = Files.getAll();
	List<Index> indexes = Index.getAll();
	log.info("sizes " + files.size() + " " + indexes.size());
	Map<String, String> filesMapMd5 = new HashMap<String, String>();
	Map<String, String> filesMapFilename = new HashMap<String, String>();
	for (Files file : files) {
	    String filename = file.getFilename();
	    String md5 = file.getMd5();
	    if (md5 == null) {
		retlist.add("No md5 " + filename);
		continue;
	    }
	    filesMapMd5.put(md5, filename);
	    filesMapFilename.put(filename, md5);
	}
	for (Index index : indexes) {
	    Boolean indexed = index.getIndexed();
	    if (indexed != null) {
		if (indexed.booleanValue()) {
		    continue;
		}
	    }
	    String md5 = index.getMd5();
	    String filename = filesMapMd5.get(md5);
	    retlist.add(filename);
	}
	return retlist;
    }

    //private static int doTika(String dbfilename, String filename, String md5, Index index, List<String> retlist) {
    public static void doTika() {
	TikaQueueElement el = Queues.tikaQueue.poll();
	if (el == null) {
		log.error("empty queue");
	    return;
	}
	// vulnerable spot
	//Queues.incTikas();
	//Queues.tikaRunQueue.add(el);
	long now = new Date().getTime();
	try {
	String dbfilename = el.dbfilename;
	String filename = el.filename;
	String md5 = el.md5;
	Index index = el.index;
	List<String> retlist = el.retlist;
	log.info("incTikas " + dbfilename);
	Queues.tikaTimeoutQueue.add(dbfilename);
	int size = 0;
	try {
	    TikaHandler tika = new TikaHandler();
	    OutputStream outputStream = tika.process(filename);
	    InputStream inputStream =new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
	    size = ((ByteArrayOutputStream)outputStream).size();
	    log.info("size1 " + size);
	    BufferedInputStream bis = new BufferedInputStream(inputStream);

		long time = new Date().getTime() - now;
		log.info("timerStop filename " + time);
		retlist.add("tika handling filename " + dbfilename + " " + size + " : " + time);
	    int limit = mylimit(dbfilename);
	    if (size > limit) {
		    log.info("sizes " + size + " " + limit);
			log.info("handling filename " + dbfilename + " " + size + " : " + time);
		    //size = SearchLucene.indexme("all", md5, inputStream);
	    	IndexQueueElement elem = new IndexQueueElement("all", md5, inputStream, index, retlist, dbfilename);
	    	Queues.indexQueue.add(elem);
	    } else {
	    	if (dbfilename.equals(filename)) {
	    	    el.size = size;
	    	    Queues.otherQueue.add(el);
	    	} else {
	    		log.info("Too small " + filename + " " + md5 + " " + size + " " + limit);
	    		retlist.add("Too small " + dbfilename + " " + md5 + " " + size);
	    	}
	    }
	    
	    outputStream.close();
	} catch (Exception e) {
	    log.error("Exception", e);
	} finally {
	    //stream.close();            // close the stream
	}
	//Queues.decTikas();
	//Queues.tikaRunQueue.remove(el);
	
	boolean success = Queues.tikaTimeoutQueue.remove(dbfilename);
	if (!success) {
		log.error("queue not having " + dbfilename);
	}
	} catch (Exception e) {
		log.error("Exception", e);
	}
	finally {
		//log.info("ending");
	}
    }

    private static int mylimit(String filename) {
	String lowercase = filename.toLowerCase();
	if (lowercase.endsWith(".pdf")) {
	    return 4096;
	}
	if (lowercase.endsWith(".djvu") || lowercase.endsWith(".djv")) {
	    return 4096;
	}
	if (lowercase.endsWith(".mp3")) {
	    return 16;
	}
	return 4096;
    }
    
}