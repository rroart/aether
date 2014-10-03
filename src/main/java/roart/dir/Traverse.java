package roart.dir;

import java.util.Map;
import java.io.*;
import roart.content.*;
import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.search.*;
import roart.thread.TikaRunner;
import roart.dao.IndexFilesDao;
import roart.dao.ClassifyDao;
import roart.model.IndexFiles;
import roart.model.FileLocation;
import roart.model.ResultItem;
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

import org.apache.tika.metadata.Metadata;

public class Traverse {

    private static int MAXFILE = 500;

    private static Log log = LogFactory.getLog("Traverse");

    private static boolean indirlistnot(String filename, String[] dirlistnot) {
	if (dirlistnot == null) {
	    return false;
	}
	for (int i = 0; i < dirlistnot.length; i++) {
	    if (filename.indexOf(dirlistnot[i])>=0) {
		return true;
	    }
	}
	return false;
    }

    public static Set<String> doList (String dirname, Set<String> newset, Map<String, HashSet<String>> dirset, String[] dirlistnot, boolean newmd5) throws Exception {
	Set<String> retset = new HashSet<String>();
	if (indirlistnot(dirname, dirlistnot)) {
	    return retset;
	}
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
		retset.addAll(doList(filename, newset, dirset, dirlistnot, newmd5));
	    } else {
		//log.info("retset " + filename);
		retset.add(filename);
		//Reader reader = new ParsingReader(parser, stream, ...);
		//Files files = Files.ensureExistence(filename);
		String curMd5 = IndexFilesDao.getMd5ByFilename(filename);;
		//files.setTouched(Boolean.TRUE);
		/*
		if (files != null) {
		    curMd5 = files.getMd5();
		    if (curMd5 != null && curMd5.length() == 0) {
			curMd5 = null;
			log.info("set curmd5 null");
		    }
		}
		*/
		if (newmd5 == true || curMd5 == null) {
		    try {
			FileInputStream fis = new FileInputStream( new File(filename));
			String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex( fis );
			IndexFiles files = null;
			if (files == null) {
			    files = IndexFilesDao.getByMd5(md5);
			}
			files.addFile(filename);
			IndexFilesDao.save(files);
			IndexFilesDao.flush();
			log.info("adding md5 file " + filename);
			if (newset != null) {
			    if (curMd5 == null || (newmd5 == true && !curMd5.equals(md5))) {
				newset.add(filename);
			    }
			}
		    } catch (Exception e) {
			log.info("Error: " + e.getMessage());
			log.error("Exception", e);
		    }
		}
		md5set.add(curMd5);
	    }
	}
	dirset.put(dirname, md5set);
	//log.info("retsize " + retset.size());
	return retset;
    }

    public static Set<String> doList2 (Map<String, HashSet<String>> dirset, Map<String, HashSet<String>> fileset) throws Exception {
	Set<String> retset = new HashSet<String>();

	List<IndexFiles> files = IndexFilesDao.getAll();
	log.info("size " + files.size());
	for (IndexFiles file : files) {
	    String md5 = file.getMd5();
	    for (FileLocation filename : file.getFilelocations()) {
		File tmpfile = new File(filename.getFilename());
		String dirname = tmpfile.getParent();
		HashSet<String> md5set = dirset.get(dirname);
		if (md5set == null) {
		    md5set = new HashSet<String>();
		    dirset.put(dirname, md5set);
		}
		md5set.add(md5);

		HashSet<String> dir5set = fileset.get(md5);
		if (dir5set == null) {
		    dir5set = new HashSet<String>();
		    fileset.put(md5, dir5set);
		}
		dir5set.add(dirname);
	    }
	}
	return retset;
    }

    // old, probably oudated by overlapping?
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
		String md5 = IndexFilesDao.getMd5ByFilename(filename);
		IndexFiles files = IndexFilesDao.getByMd5(md5);
		if (files == null) {
		    error = true;
		    continue;
		}
		if (md5 == null) {
		    error = true;
		    continue;
		}
		if (IndexFilesDao.getByMd5(md5).getFilelocations().size() < 2) {
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

    public static List<List> index(String suffix) throws Exception {
	List<List> retlistlist = new ArrayList<List>();
	List<ResultItem> retlist = new ArrayList<ResultItem>();
	List<ResultItem> retlistnot = new ArrayList<ResultItem>();
	String maxStr = roart.util.Prop.getProp().getProperty("failedlimit");
        int max = new Integer(maxStr).intValue();
	List<IndexFiles> indexes = IndexFilesDao.getAll();
	log.info("sizes " + indexes.size());
	Map<String, String> filesMapMd5 = new HashMap<String, String>();
	Map<String, String> filesMapFilename = new HashMap<String, String>();
	for (IndexFiles index : indexes) {
	    String md5 = index.getMd5();
	    for (FileLocation filename : index.getFilelocations()) {
		if (suffix != null && !filename.getFilename().endsWith(suffix)) {
		    continue;
		}
		filesMapMd5.put(md5, filename.toString());
		filesMapFilename.put(filename.toString(), md5);
	    }
	}
	Map<String, Boolean> indexMap = new HashMap<String, Boolean>();
	for (IndexFiles index : indexes) {
	    indexMap.put(index.getMd5(), index.getIndexed());
	}
	for (String md5 : filesMapMd5.keySet()) {
	    indexsingle(retlist, retlistnot, md5, indexMap, filesMapMd5, false, max);
	}
	retlistlist.add(retlist);
	retlistlist.add(retlistnot);
	return retlistlist;
    }

    public static List<List> index(String add, boolean reindex) throws Exception {
	List<List> retlistlist = new ArrayList<List>();
	List<ResultItem> retlist = new ArrayList<ResultItem>();
	List<ResultItem> retlistnot = new ArrayList<ResultItem>();
	List<ResultItem> retlist2 = new ArrayList<ResultItem>();
	String maxStr = roart.util.Prop.getProp().getProperty("failedlimit");
        int max = new Integer(maxStr).intValue();
	Set<String> md5set = new HashSet<String>();
	String dirname = add;
	File dir = new File(dirname);
	if (!dir.exists()) {
	    retlist2.add(new ResultItem("File " + add + " does not exist"));
	    retlistlist.add(retlist);
	    retlistlist.add(retlist2);
	    retlistlist.add(retlistnot);
	    return retlistlist;
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
		List<List> retlistlist2 = index(filename, reindex);
		retlist.addAll(retlistlist2.get(0));
		retlist2.addAll(retlistlist2.get(1));
		retlistnot.addAll(retlistlist2.get(2));
	    } else {
		//log.info("retset " + filename);
		//Reader reader = new ParsingReader(parser, stream, ...);
		String md5 = IndexFilesDao.getMd5ByFilename(filename);
		if (md5 == null) {
		    log.error("filename md5 null for " + filename);
		    continue;
		}
		IndexFiles files = IndexFilesDao.getByMd5(md5);
		//files.setTouched(Boolean.TRUE);
		if (files == null || files.getMd5() == null) {
		    continue;
		}
		//retlist.add(new ResultItem(filename));
		IndexFiles index = files;

		Map<String, String> filesMapMd5 = new HashMap<String, String>();
		filesMapMd5.put(files.getMd5(), files.getFilename());

		Map<String, Boolean> indexMap = new HashMap<String, Boolean>();
		if (index != null) {
		    indexMap.put(index.getMd5(), index.getIndexed());
		}

		indexsingle(retlist, retlistnot, md5, indexMap, filesMapMd5, reindex, max);
	    }
	    log.info("file " + filename);
	}
	retlistlist.add(retlist);
	retlistlist.add(retlist2);
	retlistlist.add(retlistnot);
	return retlistlist;
    }

    public static List<List> reindexdate(String date) throws Exception {
	boolean reindex = true;
	List<List> retlistlist = new ArrayList<List>();
	List<ResultItem> retlist = new ArrayList<ResultItem>();
	List<ResultItem> retlistnot = new ArrayList<ResultItem>();
	Set<String> md5set = new HashSet<String>();
        List<IndexFiles> indexes = IndexFilesDao.getAll();
	int i = 0;
	String maxStr = roart.util.Prop.getProp().getProperty("reindexlimit");
	int max = new Integer(maxStr).intValue();
	Long ts = new Long(date);
	for (IndexFiles index : indexes) {
	    Boolean indexed = index.getIndexed();
            if (indexed == null || indexed.booleanValue() == false) {
		continue;
            }
	    String md5 = index.getMd5();
	    String timestamp = index.getTimestamp();
	    if (timestamp != null) {
		if (new Long(timestamp).compareTo(ts) >= 0) {
		    continue;
		}
	    }
	    IndexFiles files = IndexFilesDao.getByMd5(md5);
	    String filename = files.getFilename();

	    if (filename == null) {
		log.error("md5 filename null " + md5);
		continue;
	    }

	    i++;
	    if (max > 0 && i > max) {
		break;
	    }

	    //retlist.add(new ResultItem(filename));

	    Map<String, String> filesMapMd5 = new HashMap<String, String>();
	    filesMapMd5.put(md5, filename);

	    Map<String, Boolean> indexMap = new HashMap<String, Boolean>();
	    if (index != null) {
		indexMap.put(md5, index.getIndexed());
	    }

	    indexsingle(retlist, retlistnot, md5, indexMap, filesMapMd5, reindex, 0);
	    log.info("file " + filename);
	}
	retlistlist.add(retlist);
	retlistlist.add(retlistnot);
	return retlistlist;
    }

    public static void indexsingle(List<ResultItem> retlist, List<ResultItem> retlistnot, String md5, Map<String, Boolean> indexMap, Map<String, String> filesMapMd5, boolean reindex, int max) throws Exception {
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
	    IndexFiles index = IndexFilesDao.getByMd5(md5);

	    if (!reindex && max > 0) {
		int failed = index.getFailed();
		if (failed >= max) {
		    log.info("failed too much for " + md5);
		    return;
		}
	    }

	    //InputStream stream = null;
	    index.setTimeoutreason("");
	    index.setFailedreason("");
	    int size = 0;
	    TikaQueueElement e = new TikaQueueElement(filename, filename, md5, index, retlist, retlistnot, new Metadata());
	    Queues.tikaQueue.add(e);
	    //size = doTika(filename, filename, md5, index, retlist);
    }

    public static List<ResultItem> notindexed() throws Exception {
	List<ResultItem> retlist = new ArrayList<ResultItem>();
	ResultItem ri = new ResultItem();
	ri.add("Md5/Id");
	ri.add("Timestamp");
	ri.add("Indexed time");
	ri.add("Convertsw");
	ri.add("Converttime");
	ri.add("Failed");
	ri.add("Failed reason");
	ri.add("Timeout reason");
	ri.add("No indexing reason");
	ri.add("Filenames");
	ri.add("A filename");
	retlist.add(ri);
	List<IndexFiles> indexes = IndexFilesDao.getAll();
	log.info("sizes " + indexes.size());
	Map<String, String> filesMapMd5 = new HashMap<String, String>();
	Map<String, String> filesMapFilename = new HashMap<String, String>();
	for (IndexFiles index : indexes) {
	    String md5 = index.getMd5();
	    filesMapMd5.put(md5, index.getFilename());
	    Boolean indexed = index.getIndexed();
	    if (indexed != null && indexed.booleanValue() == true) {
		continue;
	    }
	    String afilename = null;
	    for (FileLocation filename : index.getFilelocations()) {
		afilename = filename.getFilename();
		if (true) {
		    break;
		}
		filesMapFilename.put(filename.toString(), md5);
		if (indexed != null) {
		    if (!indexed.booleanValue()) {
			String name = filename.toString();
			if (name != null) {
			    name = name.replace('<',' ');
			    name = name.replace('>',' ');
			    //retlist.add(name);
			}
		    }
		}
	    }
	    ri = new ResultItem();
	    ri.add(index.getMd5());
	    ri.add(index.getTimestampDate().toString());
	    ri.add(index.getTimeindex("%.2f"));
	    ri.add(index.getConvertsw());
	    ri.add(index.getConverttime("%.2f"));
	    ri.add("" + index.getFailed());
	    ri.add(index.getFailedreason());
	    ri.add(index.getTimeoutreason());
	    ri.add(index.getNoindexreason());
	    ri.add("" + index.getFilelocations().size());
	    ri.add(afilename);
	    retlist.add(ri);
	}
	return retlist;
    }

    public static List<ResultItem> indexed() throws Exception {
	List<ResultItem> retlist = new ArrayList<ResultItem>();
	List<IndexFiles> indexes = IndexFilesDao.getAll();
	log.info("sizes " + indexes.size());
	Map<String, String> filesMapMd5 = new HashMap<String, String>();
	Map<String, String> filesMapFilename = new HashMap<String, String>();
	for (IndexFiles index : indexes) {
	    String md5 = index.getMd5();
	    filesMapMd5.put(md5, index.getFilename());
	    Boolean indexed = index.getIndexed();
	    for (FileLocation filename : index.getFilelocations()) {
		filesMapFilename.put(filename.toString(), md5);
		if (indexed != null) {
		    if (indexed.booleanValue()) {
			String name = filename.toString();
			if (name != null) {
			    name = name.replace('<',' ');
			    name = name.replace('>',' ');
			    retlist.add(new ResultItem(name));
			}
		    }
		}
	    }
	}
	return retlist;
    }

    //private static int doTika(String dbfilename, String filename, String md5, Index index, List<String> retlist) {
    public static void doTika(TikaQueueElement el) {
    	/*
	TikaQueueElement el = Queues.tikaQueue.poll();
	if (el == null) {
		log.error("empty queue");
	    return;
	}
	*/
	// vulnerable spot
	//Queues.incTikas();
	//Queues.tikaRunQueue.add(el);
	long now = System.currentTimeMillis();
	try {
	String dbfilename = el.dbfilename;
	String filename = el.filename;
	String md5 = el.md5;
	IndexFiles index = el.index;
	List<ResultItem> retlist = el.retlist;
	List<ResultItem> retlistnot = el.retlistnot;
	Metadata metadata = el.metadata;
	log.info("incTikas " + dbfilename);
	Queues.tikaTimeoutQueue.add(dbfilename);
	int size = 0;
	try {
	    TikaHandler tika = new TikaHandler();
	    OutputStream outputStream = tika.process(filename, metadata);
	    InputStream inputStream =new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
	    size = ((ByteArrayOutputStream)outputStream).size();
	    log.info("size1 " + size);
	    BufferedInputStream bis = new BufferedInputStream(inputStream);

	    long time = System.currentTimeMillis() - now;
	    el.index.setConverttime(time);
	    log.info("timerStop filename " + time);
	    //retlist.add(new ResultItem(new String("tika handling filename " + dbfilename + " " + size + " : " + time)));
	    int limit = mylimit(dbfilename);
	    if (size > limit) {
		log.info("sizes " + size + " " + limit);
		log.info("handling filename " + dbfilename + " " + size + " : " + time);

		String content = getString(inputStream);

		String lang = LanguageDetect.detect(content);
		if (lang != null && lang.equals("en")) {
		    now = System.currentTimeMillis();
		    String classification = ClassifyDao.classify(content);
		    time = System.currentTimeMillis() - now;
		    log.info("classtime " + time);
		    //System.out.println("classtime " + time);
		    el.index.setTimeclass(time);
		    el.index.setClassification(classification);
		}

		//size = SearchLucene.indexme("all", md5, inputStream);
		IndexQueueElement elem = new IndexQueueElement("all", md5, inputStream, index, retlist, retlistnot, dbfilename, metadata);
		elem.lang = lang;
		elem.content = content;
		if (el.convertsw != null) {
		    elem.convertsw = el.convertsw;
		} else {
		    elem.convertsw = "tika";
		}
		Queues.indexQueue.add(elem);
	    } else {
	    	if (dbfilename.equals(filename)) {
	    	    el.size = size;
	    	    Queues.otherQueue.add(el);
	    	} else {
		    log.info("Too small " + filename + " " + md5 + " " + size + " " + limit);
		    String myclassify = roart.util.Prop.getProp().getProperty("myclassify");
		    boolean doclassify = myclassify != null && myclassify.length() > 0;
		    ResultItem ri = new ResultItem();
		    ri.add("too small");
		    ri.add(md5);
		    ri.add(dbfilename);
		    ri.add("n/a");
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
		    Boolean isIndexed = index.getIndexed();
		    if (isIndexed == null || isIndexed.booleanValue() == false) {
			index.incrFailed();
			//index.save();
		    }
	    	}
	    }   
	    outputStream.close();
	} catch (Exception e) {
	    el.index.setFailedreason(el.index.getFailedreason() + "tika exception " + e.getClass().getName() + " ");
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
		log.info("ending " + el.dbfilename);
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
	if (lowercase.endsWith(".flac")) {
	    return 16;
	}
	return 4096;
    }

    private static String getString(InputStream inputStream) {
	try {
	    DataInputStream in = new DataInputStream(inputStream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line = null;
    // index some data
	    StringBuilder result = new StringBuilder();
	    while ((line = br.readLine()) != null) {
		result.append(line);
	    }
	    return  result.toString();
	} catch (Exception e) {
	    log.error("Exception", e);
	    return null;
	}
    }

    public static ResultItem getHeader() {
	String myclassify = roart.util.Prop.getProp().getProperty("myclassify");
	boolean doclassify = myclassify != null && myclassify.length() > 0;

    ResultItem ri = new ResultItem();
    ri.add("Indexed");
    ri.add("Md5/Id");
    ri.add("Filename");
    ri.add("Lang");
    if (doclassify) {
	ri.add("Classification");
    }
    ri.add("Timestamp");
    ri.add("Convertsw");
    ri.add("Converttime");
    ri.add("Indextime");
    if (doclassify) {
	ri.add("Classificationtime");
    }
    ri.add("Failed");
    ri.add("Failed reason");
    ri.add("Timeout reason");
    ri.add("No indexing reason");
    return ri;
    }
    
    public static ResultItem getHeaderNot() {
	String myclassify = roart.util.Prop.getProp().getProperty("myclassify");
	boolean doclassify = myclassify != null && myclassify.length() > 0;

    ResultItem ri = new ResultItem();
    ri.add("Indexed");
    ri.add("Md5/Id");
    ri.add("Filename");
    ri.add("Lang");
    if (doclassify) {
	ri.add("Classification");
    }
    ri.add("Timestamp");
    ri.add("Convertsw");
    ri.add("Converttime");
    ri.add("Indextime");
    if (doclassify) {
	ri.add("Classificationtime");
    }
    ri.add("Failed");
    ri.add("Failed reason");
    ri.add("Timeout reason");
    ri.add("No indexing reason");
    return ri;
    }
    
}
