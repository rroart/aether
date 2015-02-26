package roart.service;

import roart.model.ResultItem;

import javax.servlet.http.*;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import java.util.concurrent.TimeUnit;

import java.io.*;

import roart.dir.Traverse;

import roart.queue.ClientQueueElement;
import roart.queue.ClientQueueElement.Function;

import roart.model.FileLocation;
import roart.model.FileObject;
import roart.model.IndexFiles;
import roart.model.SearchDisplay;
import roart.queue.Queues;
import roart.search.SearchDao;
import roart.thread.ControlRunner;
import roart.thread.IndexRunner;
import roart.thread.OtherRunner;
import roart.thread.TikaRunner;
import roart.content.OtherHandler;
import roart.content.ClientHandler;
import roart.thread.ClientRunner;
import roart.thread.DbRunner;
import roart.thread.ZKRunner;
import roart.util.ConfigConstants;
import roart.util.Constants;
import roart.zkutil.ZKLockUtil;
import roart.zkutil.ZKMessageUtil;
import roart.zkutil.ZKWriteLock;
import roart.zkutil.ZKBlockWriteLock;

import roart.database.IndexFilesDao;
import roart.filesystem.FileSystemDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static volatile Integer writelock = new Integer(-1);

    private static int dirsizelimit = 100;

    // called from ui
    // returns list: new file
    public void traverse(String add) throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.FILESYSTEM, add, null, null, null, false, false);
	Queues.clientQueue.add(e);
    }

    public Set<String> traverse(String add, Set<IndexFiles> newset, List<ResultItem> retList, Set<String> notfoundset, boolean newmd5, boolean nodbchange, boolean returnonlyold) throws Exception {
	Map<String, HashSet<String>> dirset = new HashMap<String, HashSet<String>>();
	Set<String> filesetnew2 = new HashSet<String>();
	Set<String> filesetnew = Traverse.doList(add, newset, filesetnew2, dirset, null, notfoundset, newmd5, false, nodbchange, returnonlyold);    
	for (String s : filesetnew2) {
	    retList.add(new ResultItem(s));
	}
	return filesetnew;
    }

    // called from ui
    // returns list: new file
    public void traverse() throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.FILESYSTEM, null, null, null, null, false, false);
	Queues.clientQueue.add(e);
    }

    public Set<String> traverse(Set<IndexFiles> newindexset, List<ResultItem> retList, Set<String> notfoundset, boolean newmd5, boolean nodbchange, boolean returnonlyold) throws Exception {
	Set<String> filesetnew = new HashSet<String>();
	retList.addAll(filesystem(newindexset, filesetnew, null, notfoundset, newmd5, nodbchange, returnonlyold));
	return filesetnew;
    }

    static String[] dirlist = null;
    static String[] dirlistnot = null;

    static public String nodename = "localhost";
    
    public static void parseconfig() {
	System.out.println("config2 parsed");
	//log.info("config2 parsed");
	nodename  = roart.util.Prop.getProp().getProperty(ConfigConstants.NODENAME);
	if (nodename == null || nodename.length() == 0) {
		nodename = "localhost";
	}
	String dirliststr = roart.util.Prop.getProp().getProperty(ConfigConstants.DIRLIST);
	String dirlistnotstr = roart.util.Prop.getProp().getProperty(ConfigConstants.DIRLISTNOT);
	dirlist = dirliststr.split(",");
	dirlistnot = dirlistnotstr.split(",");
    }

    private List<ResultItem> filesystem(Set<IndexFiles> indexnewset, Set<String> filesetnew, Set<String> newset, Set<String> notfoundset, boolean newmd5, boolean nodbchange, boolean returnonlyold) {
	List<ResultItem> retList = new ArrayList<ResultItem>();

	Map<String, HashSet<String>> dirset = new HashMap<String, HashSet<String>>();
	try {
	    for (int i = 0; i < dirlist.length; i ++) {
		Set<String> filesetnew2 = Traverse.doList(dirlist[i], indexnewset, newset, dirset, dirlistnot, notfoundset, newmd5, false, nodbchange, returnonlyold);
		filesetnew.addAll(filesetnew2);
	    }
	} catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	}
	return retList;
    }

    // called from ui
    public void overlapping() {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.OVERLAPPING, null, null, null, null, false, false);
	Queues.clientQueue.add(e);
    }

    @SuppressWarnings("rawtypes")
	public List<List> overlappingDo() {
	List<ResultItem> retList = new ArrayList<ResultItem>();
	List<ResultItem> retList2 = new ArrayList<ResultItem>();
	ResultItem ri = new ResultItem();
	ri.add("Percent");
	ri.add("Count");
	ri.add("Directory 1");
	ri.add("Directory 2");
	retList.add(ri);
	ri = new ResultItem();
	ri.add("Percent");
	ri.add("Count");
	ri.add("Count 2");
	ri.add("Directory");
	retList2.add(ri);

	Set<String> filesetnew = new HashSet<String>();
	Map<Integer, List<String[]>> sortlist = new TreeMap<Integer, List<String[]>>();
	Map<Integer, List<String[]>> sortlist2 = new TreeMap<Integer, List<String[]>>();
	Map<String, HashSet<String>> dirset = new HashMap<String, HashSet<String>>();
	Map<String, HashSet<String>> fileset = new HashMap<String, HashSet<String>>();

	// filesetnew/2 will be empty before and after
	// dirset will contain a map of directories, and the md5 files is contains
	// fileset will contain a map of md5 and the directories it has files in
	try {
	    Set<String> filesetnew2 = Traverse.doList2(dirset, fileset);
	    filesetnew.addAll(filesetnew2);
	} catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	}

	log.info("dirs " + dirset.size());
	log.info("files " + fileset.size());

	// start at i+1 to avoid comparing twice

	List<String> keyList = new ArrayList<String>(dirset.keySet());
	for (int i = 0; i < keyList.size(); i++ ) {
	    if (dirset.get(keyList.get(i)).size() < dirsizelimit) {
		continue;
	    }
	    for (int j = i+1; j < keyList.size(); j++ ) {
		// set1,3 with md5 files contained in dir number i
		// set2,4 with md5 files contained in dir number j
		HashSet<String> set1 = (HashSet<String>) dirset.get(keyList.get(i)).clone();
		HashSet<String> set2 = (HashSet<String>) dirset.get(keyList.get(j)).clone();
		int size0 = set1.size();
		if (set2.size() > size0) {
		    size0 = set2.size();
		}
		set1.retainAll(set2);
		// sum
		int size = set1.size();
		if (size0 == 0) {
		    size0 = 1000000;
		    log.error("size0");
		}
		// add result
		int ratio = (int) (100*size/size0);
		if (ratio > 50 && size > 4) {
		    Integer intI = new Integer(ratio);
		    String sizestr = "" + size;
		    sizestr = "      ".substring(sizestr.length()) + sizestr;
		    String[] str = new String[]{ sizestr, keyList.get(i), keyList.get(j)}; // + " " + set1;
		    List<String[]> strSet = sortlist.get(intI);
		    if (strSet == null) {
			strSet = new ArrayList<String[]>();
		    }
		    strSet.add(str);
		    sortlist.put(intI, strSet);
		}
	    }
	}
	// get biggest overlap
	for (Integer intI : sortlist.keySet()) {
	    for (String[] strs : sortlist.get(intI)) {
		ResultItem ri2 = new ResultItem();
		ri2.add("" + intI.intValue());
		for (String str : strs) {
		    ri2.add(str);
		}
		retList.add(ri2);
	    }
	}
	for (int i = 0; i < keyList.size(); i++ ) {
	    int fileexist = 0;
	    String dirname = keyList.get(i);
	    Set<String> dirs = dirset.get(dirname);
	    int dirsize = dirs.size();
	    for (String md5 : dirs) {
		Set<String> files = fileset.get(md5);
		if (files != null && files.size() >= 2) {
		    fileexist++;
		}
	    }
	    int ratio = (int) (100*fileexist/dirsize);
	    // overlapping?
	    if (ratio > 50 && dirsize > dirsizelimit) {
		Integer intI = new Integer(ratio);
		String sizestr = "" + dirsize;
		sizestr = "      ".substring(sizestr.length()) + sizestr;
		String[] str = new String[]{sizestr, "" + fileexist, dirname};
		List<String[]> strSet = sortlist2.get(intI);
		if (strSet == null) {
		    strSet = new ArrayList<String[]>();
		}
		strSet.add(str);
		sortlist2.put(intI, strSet);
	    }
	}
	for (Integer intI : sortlist2.keySet()) {
	    for (String[] strs : sortlist2.get(intI)) {
                ResultItem ri2 = new ResultItem();
                ri2.add("" + intI.intValue());
		for (String str : strs) {
		    ri2.add(str);
		}
		retList2.add(ri2);
	    }
	}
	List<List> retlistlist = new ArrayList<List>();
	retlistlist.add(retList);
	retlistlist.add(retList2);
	return retlistlist;
    }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: not indexed
    // returns list: deleted
    public void index(String suffix) throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.REINDEXSUFFIX, null, suffix, null, null, true, false);
	Queues.clientQueue.add(e);
    }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: file does not exist
    // returns list: not indexed
    public void index(String add, boolean reindex) throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.INDEX, add, null, null, null, reindex, false);
	Queues.clientQueue.add(e);
    }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: not indexed
    public void reindexdatelower(String date, boolean reindex) throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.REINDEXDATE, null, null, date, null, reindex, false);
	Queues.clientQueue.add(e);
    }

    public void reindexdatehigher(String date, boolean reindex) throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.REINDEXDATE, null, null, null, date, reindex, false);
	Queues.clientQueue.add(e);
    }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: file does not exist
    // returns list: not indexed
    public void reindexlanguage(String lang) throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.REINDEXLANGUAGE, null, lang, null, null, true, false);
	Queues.clientQueue.add(e);
    }

    @SuppressWarnings("rawtypes")
	public List<List> clientDo(ClientQueueElement el) throws Exception {
		    synchronized (writelock) {
			ZKBlockWriteLock writelock = null;
			if (zookeeper != null) {
			    writelock = ZKLockUtil.blocklockme();
			}
	Function function = el.function;
	String filename = el.file;
	boolean reindex = el.reindex;
	boolean newmd5 = el.md5change;
	log.info("function " + function + " " + filename + " " + reindex);

	SearchDisplay display = SearchService.getSearchDisplay(el.ui);
	
	Set<List> retlistset = new HashSet<List>();
	List<List> retlistlist = new ArrayList<List>();
	List<ResultItem> retList = new ArrayList<ResultItem>();
	retList.add(IndexFiles.getHeader(display));
	List<ResultItem> retTikaTimeoutList = new ArrayList<ResultItem>();
	retTikaTimeoutList.add(new ResultItem("Tika timeout"));
	List<ResultItem> retNotList = new ArrayList<ResultItem>();
	retNotList.add(IndexFiles.getHeader(display));
	List<ResultItem> retNewFilesList = new ArrayList<ResultItem>();
	retNewFilesList.add(new ResultItem("New file"));
	List<ResultItem> retDeletedList = new ArrayList<ResultItem>();
	retDeletedList.add(new ResultItem("Deleted"));
	List<ResultItem> retNotExistList = new ArrayList<ResultItem>();
	retNotExistList.add(new ResultItem("File does not exist"));

	Set<String> notfoundset = new HashSet<String>();
	Set<String> filesetnew = new HashSet<String>();
	Set<IndexFiles> indexnewset = new HashSet<IndexFiles>();

	Set<String> fileset = new HashSet<String>();
	Set<String> md5set = new HashSet<String>();

	List<List> retlisttmp = null;

	// filesystem
	// reindexsuffix
	// index
	// reindexdate
	// filesystemlucenenew

	//DbRunner.doupdate = false;
	if (function == Function.FILESYSTEM || function == Function.FILESYSTEMLUCENENEW || (function == Function.INDEX && filename != null /*&& !reindex*/)) {
	    if (filename != null) {
		//boolean nodbchange = reindex;
		//boolean returnonlyold = reindex;
		boolean nodbchange = function == Function.INDEX;
		boolean returnonlyold = function == Function.INDEX;
		filesetnew = traverse(filename, indexnewset, retNewFilesList, notfoundset, newmd5, nodbchange, returnonlyold);
	    } else {
		//boolean nodbchange = reindex;
		//boolean returnonlyold = reindex;
		boolean nodbchange = function == Function.INDEX;
		boolean returnonlyold = function == Function.INDEX;
		filesetnew = traverse(indexnewset, retNewFilesList, notfoundset, newmd5, nodbchange, returnonlyold);
	    }
	    for (String file : notfoundset) {
	    	retNotExistList.add(new ResultItem(file));
	    }
	    if (function == Function.FILESYSTEM) {
		//IndexFilesDao.commit();
		while (IndexFilesDao.dirty() > 0) {
		    TimeUnit.SECONDS.sleep(60);
		}
		retlistlist.add(retNewFilesList);
		//DbRunner.doupdate = true;
		if (zookeeper != null) {
		    ZKMessageUtil.dorefresh();
		    ZKLockUtil.unlockme(writelock);
		    ClientRunner.notify("Sending refresh request");
		}
		return  retlistlist;
	    }
	}

	Collection<IndexFiles> indexes = null;
	if (function == Function.FILESYSTEMLUCENENEW) {
	    indexes = indexnewset;
	} else if (function == Function.INDEX && filename != null /*&& !reindex*/) {
	    Set<IndexFiles> indexset = new HashSet<IndexFiles>();
	    for (String name : filesetnew) {
		String md5 = IndexFilesDao.getMd5ByFilename(name);
		IndexFiles index = IndexFilesDao.getByMd5(md5);
		if (index != null) {
		indexset.add(index);
		} else {
		    log.info("No index for " + md5 + " and " + name);
		}
	    }
	    indexes = indexset;
	} else {
	    indexes = IndexFilesDao.getAll();
	}
	//DbRunner.doupdate = true;

	String maxfailedStr = roart.util.Prop.getProp().getProperty(ConfigConstants.FAILEDLIMIT);
	int maxfailed = new Integer(maxfailedStr).intValue();

	String maxStr = roart.util.Prop.getProp().getProperty(ConfigConstants.REINDEXLIMIT);
	int max = new Integer(maxStr).intValue();

	String maxindexStr = roart.util.Prop.getProp().getProperty(ConfigConstants.INDEXLIMIT);
	int maxindex = new Integer(maxindexStr).intValue();

	Set<IndexFiles> toindexset = new HashSet<IndexFiles>();

	int i = 0;
	for (IndexFiles index : indexes) {

	    // skip if indexed already, and no reindex wanted
	    Boolean indexed = index.getIndexed();
	    if (indexed != null) {
		if (!reindex && indexed.booleanValue()) {
		    continue;
		}
	    }
	    
	    String md5 = index.getMd5();

	    // if ordinary indexing (no reindexing)
	    // and a failed limit it set
	    // and the file has come to that limit

	    if (!reindex && maxfailed > 0 && maxfailed <= index.getFailed().intValue()) {
		continue;
	    }
	    
	    if (function == Function.REINDEXDATE) {
		i += Traverse.reindexdateFilter(el, index, toindexset, fileset, md5set);
	    }
	    if (function == Function.REINDEXSUFFIX) {
		i += Traverse.reindexsuffixFilter(el, index, el.suffix, toindexset, fileset, md5set);
	    }
	    if (function == Function.INDEX || function == Function.FILESYSTEMLUCENENEW) {
		i += Traverse.indexnoFilter(el, index, reindex, toindexset, fileset, md5set);
	    }
	    if (function == Function.REINDEXLANGUAGE) {
		i += Traverse.reindexlanguageFilter(el, index, el.suffix, toindexset, fileset, md5set);
	    }
	    
	    if (reindex && max > 0 && i > max) {
		break;
	    }
	    
	    if (!reindex && maxindex > 0 && i > maxindex) {
		break;
	    }
	    
	}

	Map<String, String> filesMapMd5 = new HashMap<String, String>();
	Map<String, Boolean> indexMap = new HashMap<String, Boolean>();
	for (IndexFiles index : toindexset) {
	    String md5 = index.getMd5();
	    String name = Traverse.getExistingLocalFile(index);
	    if (name == null) {
	    	log.error("filename should not be null " + md5);
	    	continue;
	    }
	    filesMapMd5.put(md5, name);
	    indexMap.put(md5, index.getIndexed());
	}

	for (String md5 : filesMapMd5.keySet()) {
	    Traverse.indexsingle(retList, retNotList, md5, indexMap, filesMapMd5, reindex, 0, el.ui);
	}

	while ((Queues.queueSize() + Queues.runSize()) > 0) {
		TimeUnit.SECONDS.sleep(60);
		Queues.queueStat();
	}
	for (String ret : Queues.tikaTimeoutQueue) {
	    retTikaTimeoutList.add(new ResultItem(ret));
	}

	Queues.resetTikaTimeoutQueue();
	//IndexFilesDao.commit();
	while (IndexFilesDao.dirty() > 0) {
	    TimeUnit.SECONDS.sleep(60);
	}

	retlistlist.add(retList);
	retlistlist.add(retNotList);
	retlistlist.add(retNewFilesList);
	retlistlist.add(retDeletedList);
	retlistlist.add(retTikaTimeoutList);
	retlistlist.add(retNotExistList);
	if (zookeeper != null) {
	    ZKMessageUtil.dorefresh();
	    ZKLockUtil.unlockme(writelock);
	    ClientRunner.notify("Sending refresh request");
	}
	return retlistlist;
		    }
    }

    // outdated, did run once, had a bug which made duplicates
    public List<String> cleanup() {
	List<String> retlist = new ArrayList<String>();
	try {
	    return roart.search.SearchLucene.removeDuplicate();
	} catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	}
	return retlist;
    }

    // outdated, used once, when bug added filename instead of md5
    public List<String> cleanup2() {
	List<String> retlist = new ArrayList<String>();
	try {
	    //return roart.jpa.SearchLucene.cleanup2();
	} catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	}
	return retlist;
    }

    // old, probably oudated by overlapping?
    public List<String> cleanupfs(String dirname) {
	//List<String> retlist = new ArrayList<String>();
	Set<String> filesetnew = new HashSet<String>();
	try {
	    String[] dirlist = { dirname };
	    for (int i = 0; i < dirlist.length; i ++) {
		Set<String> filesetnew2 = Traverse.dupdir(dirlist[i]);
		filesetnew.addAll(filesetnew2);
	    }
	} catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	}
	return new ArrayList<String>(filesetnew);
    }

    // called from ui
    public void memoryusage() {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.MEMORYUSAGE, null, null, null, null, false, false);
	Queues.clientQueue.add(e);
    }

    @SuppressWarnings("rawtypes")
	public List<List> memoryusageDo() {
	List<ResultItem> retlist = new ArrayList<ResultItem>();
	try {
	    Runtime runtime = Runtime.getRuntime();
	    long maxMemory = runtime.maxMemory();
	    long allocatedMemory = runtime.totalMemory();
	    long freeMemory = runtime.freeMemory();
	    java.text.NumberFormat format = java.text.NumberFormat.getInstance();
	    retlist.add(new ResultItem("free memory: " + format.format(freeMemory / 1024)));
	    retlist.add(new ResultItem("allocated memory: " + format.format(allocatedMemory / 1024)));
	    retlist.add(new ResultItem("max memory: " + format.format(maxMemory / 1024)));
	    retlist.add(new ResultItem("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024)));
	} catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	}
	List<List> retlistlist = new ArrayList<List>();
	retlistlist.add(retlist);
	return retlistlist;
    }

    // called from ui
    // returns list: not indexed
    // returns list: another with columns
    public void notindexed() throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.NOTINDEXED, null, null, null, null, false, false);
	Queues.clientQueue.add(e);
    }

    @SuppressWarnings("rawtypes")
	public List<List> notindexedDo(ClientQueueElement el) throws Exception {
	List<List> retlistlist = new ArrayList<List>();
	List<ResultItem> retlist = new ArrayList<ResultItem>();
	List<ResultItem> retlist2 = new ArrayList<ResultItem>();
	ResultItem ri3 = new ResultItem();
	ri3.add("Column 1");
	ri3.add("Column 2");
	ri3.add("Column 3");
	retlist2.add(ri3);
	List<ResultItem> retlistyes = null;
	try {
	    retlist.addAll(Traverse.notindexed(el));
	    retlistyes = Traverse.indexed(el);
	    Map<String, Integer> plusretlist = new HashMap<String, Integer>();
	    Map<String, Integer> plusretlistyes = new HashMap<String, Integer>();
	    for(ResultItem ri : retlist) {
		if (ri == retlist.get(0)) {
		    continue;
		}
		String filename = (String) ri.get().get(10);
		if (filename == null) {
		    continue;
		}
		int ind = filename.lastIndexOf(".");
		if (ind == -1 || ind <= filename.length() - 6) {
		    continue;
		}
		String suffix = filename.substring(ind+1);
		Integer i = plusretlist.get(suffix);
		if (i == null) {
		    i = new Integer(0);
		}
		i++;
		plusretlist.put(suffix, i);
	    }
	    for(ResultItem ri : retlistyes) {
		String filename = (String) ri.get().get(0); // or for a whole list?
		if (filename == null) {
		    continue;
		}
		int ind = filename.lastIndexOf(".");
		if (ind == -1 || ind <= filename.length() - 6) {
		    continue;
		}
		String suffix = filename.substring(ind+1);
		Integer i = plusretlistyes.get(suffix);
		if (i == null) {
		    i = new Integer(0);
		}
		i++;
		plusretlistyes.put(suffix, i);
	    }
	    log.info("size " + plusretlist.size());
	    log.info("sizeyes " + plusretlistyes.size());
	    for(String string : plusretlist.keySet()) {
		ResultItem ri2 = new ResultItem();
		ri2.add("Format");
		ri2.add(string);
		ri2.add("" + plusretlist.get(string).intValue());
		retlist2.add(ri2);
	    }
	    for(String string : plusretlistyes.keySet()) {
		ResultItem ri2 = new ResultItem();
		ri2.add("Formatyes");
		ri2.add(string);
		ri2.add("" + plusretlistyes.get(string).intValue());
		retlist2.add(ri2);
	    }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
	log.info("sizes " + retlist.size() + " " + retlist2.size() + " " + System.currentTimeMillis());
	retlistlist.add(retlist);
	retlistlist.add(retlist2);
	return retlistlist;
    }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: new file
    // returns list: file does not exist
    // returns list: not indexed
    public void filesystemlucenenew() throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.FILESYSTEMLUCENENEW, null, null, null, null, false, false);
	Queues.clientQueue.add(e);
    }

    public void filesystemlucenenew(String add, boolean md5checknew) throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.FILESYSTEMLUCENENEW, add, null, null, null, false, md5checknew);
	Queues.clientQueue.add(e);
    }

    public void dbindex(String md5) throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.DBINDEX, md5, null, null, null, false, false); // dumb overload habit
	Queues.clientQueue.add(e);
    }

    @SuppressWarnings("rawtypes")
	public List<List> dbindexDo(ClientQueueElement el) throws Exception {
	Function function = el.function;
	String md5 = el.file;
	log.info("function " + function + " " + md5);

	SearchDisplay display = SearchService.getSearchDisplay(el.ui);

	List<List> retlistlist = new ArrayList<List>();
	List<ResultItem> indexList = new ArrayList<ResultItem>();
	indexList.add(IndexFiles.getHeader(display));
	List<ResultItem> indexfilesList = new ArrayList<ResultItem>();
	indexfilesList.add(new ResultItem("Files"));
	List<ResultItem> filesList = new ArrayList<ResultItem>();
	filesList.add(new ResultItem("Files"));

	IndexFiles index = IndexFilesDao.getByMd5(md5);
	if (index != null) {
	    indexList.add(IndexFiles.getResultItem(index, index.getLanguage(), display));
	    Set<FileLocation> files = index.getFilelocations();
	    if (files != null) {
		for (FileLocation filename : files) {
		    indexfilesList.add(new ResultItem(filename.toString()));
		}
	    }
	    Set<FileLocation> flSet = IndexFilesDao.getFilelocationsByMd5(md5);
	    if (flSet != null) {
		for (FileLocation fl : flSet) {
		    if (fl == null) {
			filesList.add(new ResultItem(""));
		    } else {
			filesList.add(new ResultItem(fl.toString()));
		    }
		}
	    }
			  
	}

	retlistlist.add(indexList);
	retlistlist.add(indexfilesList);
	retlistlist.add(filesList);
	return retlistlist;
    }

    public void dbsearch(String md5) throws Exception {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.DBSEARCH, md5, null, null, null, false, false); // dumb overload habit
	Queues.clientQueue.add(e);
    }

    @SuppressWarnings("rawtypes")
	public List<List> dbsearchDo(ClientQueueElement el) throws Exception {
	Function function = el.function;
	String searchexpr = el.file;
	int i = searchexpr.indexOf(":");
	log.info("function " + function + " " + searchexpr);

	List<List> retlistlist = new ArrayList<List>();
	if (i < 0) {
	    return retlistlist;
	}
	SearchDisplay display = SearchService.getSearchDisplay(el.ui);
	String field = searchexpr.substring(0, i);
	String text = searchexpr.substring(i + 1);
	List<ResultItem> indexList = new ArrayList<ResultItem>();
	indexList.add(IndexFiles.getHeader(display));

	List<IndexFiles> indexes = IndexFilesDao.getAll();
	for (IndexFiles index : indexes) {
	    boolean match = false;
	    
	    if (field.equals("indexed")) {
		Boolean indexedB = index.getIndexed();
		boolean ind = indexedB != null && indexedB.booleanValue();
		if (ind && text.equals("true")) {
		    match = true;
		}
		if (!ind && text.equals("false")) {
		    match = true;
		}
	    }
	    if (field.equals("convertsw")) {
		String convertsw = index.getConvertsw();
		if (convertsw != null) {
		    match = convertsw.contains(text);
		}
	    }
	    if (field.equals("classification")) {
		String classification = index.getClassification();
		if (classification != null) {
		    match = classification.contains(text);
		}
	    }
	    if (field.equals("failedreason")) {
		String failedreason = index.getFailedreason();
		if (failedreason != null) {
		    match = failedreason.contains(text);
		}
	    }
	    if (field.equals("noindexreason")) {
		String noindexreason = index.getNoindexreason();
		if (noindexreason != null) {
		    match = noindexreason.contains(text);
		}
	    }
	    if (field.equals("timeoutreason")) {
		String timeoutreason = index.getTimeoutreason();
		if (timeoutreason != null) {
		    match = timeoutreason.contains(text);
		}
	    }
	    if (field.equals("language")) {
		String language = index.getLanguage();
		if (language != null) {
		    match = language.equals(text);
		}
	    }
	    if (match) {
		indexList.add(IndexFiles.getResultItem(index, index.getLanguage(), display));
	    }
	}

	retlistlist.add(indexList);
	return retlistlist;
    }

    private static TikaRunner tikaRunnable = null;
    public static Thread tikaWorker = null;
    private static IndexRunner indexRunnable = null;
    public static Thread indexWorker = null;
    private static OtherRunner otherRunnable = null;
    public static Thread otherWorker = null;
    private static ClientRunner clientRunnable = null;
    public static Thread clientWorker = null;
    private static DbRunner dbRunnable = null;
    public static Thread dbWorker = null;
    private static ControlRunner controlRunnable = null;
    private static Thread controlWorker = null;
    private static ZKRunner zkRunnable = null;
    public static Thread zkWorker = null;

    public static volatile String zookeeper = null;

    public void startThreads() {
    	if (tikaRunnable == null) {
	    startTikaWorker();
    	}
    	if (indexRunnable == null) {
    	startIndexWorker();
    	}
    	if (otherRunnable == null) {
	    startOtherWorker();
    	}
    	if (clientRunnable == null) {
    	startClientWorker();
    	}
    	if (dbRunnable == null) {
    	startDbWorker();
    	}
    	if (controlRunnable == null) {
    	startControlWorker();
    	}
    	if (zookeeper != null && zkRunnable == null) {
    	startZKWorker();
    	}
    }

	private void startControlWorker() {
		controlRunnable = new ControlRunner();
    	controlWorker = new Thread(controlRunnable);
    	controlWorker.setName("ControlWorker");
    	controlWorker.start();
    	log.info("starting control worker");
	}

	public void startTikaWorker() {
		String timeoutstr = roart.util.Prop.getProp().getProperty(ConfigConstants.TIKATIMEOUT);
	    int timeout = new Integer(timeoutstr).intValue();
	    TikaRunner.timeout = timeout;

    	tikaRunnable = new TikaRunner();
    	tikaWorker = new Thread(tikaRunnable);
    	tikaWorker.setName("TikaWorker");
    	tikaWorker.start();
    	log.info("starting tika worker");
	}

	public void startIndexWorker() {
		indexRunnable = new IndexRunner();
    	indexWorker = new Thread(indexRunnable);
    	indexWorker.setName("IndexWorker");
    	indexWorker.start();
    	log.info("starting index worker");
	}

	public void startOtherWorker() {
		String timeoutstr = roart.util.Prop.getProp().getProperty(ConfigConstants.OTHERTIMEOUT);
	    int timeout = new Integer(timeoutstr).intValue();
	    OtherHandler.timeout = timeout;

    	otherRunnable = new OtherRunner();
    	otherWorker = new Thread(otherRunnable);
    	otherWorker.setName("OtherWorker");
    	otherWorker.start();
    	log.info("starting other worker");
	}

	public void startClientWorker() {
		clientRunnable = new ClientRunner();
    	clientWorker = new Thread(clientRunnable);
    	clientWorker.setName("ClientWorker");
    	clientWorker.start();
    	log.info("starting client worker");
	}

	public void startDbWorker() {
		dbRunnable = new DbRunner();
    	dbWorker = new Thread(dbRunnable);
    	dbWorker.setName("DbWorker");
    	dbWorker.start();
    	log.info("starting db worker");
	}

	public void startZKWorker() {
		zkRunnable = new ZKRunner();
    	zkWorker = new Thread(zkRunnable);
    	zkWorker.setName("ZKWorker");
    	zkWorker.start();
    	log.info("starting zk worker");
	}

    @SuppressWarnings("rawtypes")
	private List<List> mergeListSet(Set<List> listSet, int size) {
	List<List> retlistlist = new ArrayList<List>();
	for (int i = 0 ; i < size ; i++ ) {
	    List<ResultItem> retlist = new ArrayList<ResultItem>();
	    retlistlist.add(retlist);
	}
	for (List<List> listArray : listSet) {
	    for (int i = 0 ; i < size ; i++ ) {
		retlistlist.get(i).addAll(listArray.get(i));
	    }
	}
	return retlistlist;
    }

	public void consistentclean(boolean clean) {
		// TODO Auto-generated method stub
		ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.CONSISTENTCLEAN, null, null, null, null, clean, false); // more dumb overload
		Queues.clientQueue.add(e);
	    }

	    @SuppressWarnings("rawtypes")
		public List<List> consistentcleanDo(ClientQueueElement el) {
	    	boolean clean = el.reindex;
		List<ResultItem> delList = new ArrayList<ResultItem>();
		List<ResultItem> nonexistList = new ArrayList<ResultItem>();
		List<ResultItem> newList = new ArrayList<ResultItem>();
		ResultItem ri = new ResultItem("Filename delete");
		delList.add(ri);
		ri = new ResultItem("Filename nonexist");
		nonexistList.add(ri);
		ri = new ResultItem("Filename new");
		newList.add(ri);

		Set<String> delfileset = new HashSet<String>();
			    
		List<IndexFiles> indexes;
		try {
			indexes = IndexFilesDao.getAll();
		log.info("size " + indexes.size());
		for (IndexFiles index : indexes) {
			for (FileLocation fl : index.getFilelocations()) {
				if (fl.isLocal()) {
					String filename = fl.getFilename();
					FileObject fo = FileSystemDao.get(filename);
					if (!FileSystemDao.exists(fo)) {
						delList.add(new ResultItem(filename));
						delfileset.add(filename);
					}
				}
			}
		}
		
		Set<String> filesetnew = new HashSet<String>(); // just a dir list
		Set<String> newset = new HashSet<String>();
		Set<String> notfoundset = new HashSet<String>();
		
		filesystem(null, filesetnew, newset, notfoundset, false, true, false);
		
		for (String file : newset) {
			newList.add(new ResultItem(file));
		}
		for (String file : notfoundset) {
			nonexistList.add(new ResultItem(file));
		}
		
		if (clean) {
		    synchronized (writelock) {
			ZKBlockWriteLock writelock = null;
			if (zookeeper != null) {
			    writelock = ZKLockUtil.blocklockme();
			}
		    //DbRunner.doupdate = false;
			for (String filename : delfileset) {
				String md5 = IndexFilesDao.getMd5ByFilename(filename);
				if (md5 != null) {
					IndexFiles ifile = IndexFilesDao.getByMd5(md5);
					FileLocation fl = new FileLocation(filename);
					boolean removed = ifile.removeFilelocation(fl);
					//log.info("fls2 size " + removed + ifile.getFilelocations().size());
				} else {
					log.info("trying the hard way, no md5 for" + filename);
					for (IndexFiles index : indexes) {
					    FileLocation fl = new FileLocation(filename);
					    if (index.getFilelocations().contains(fl)) {
						boolean removed = index.removeFilelocation(fl);
						//log.info("fls3 size " + removed + index.getFilelocations().size());
					    }
					}
				}
			}
			//DbRunner.doupdate = true;
			//IndexFilesDao.commit();
			while (IndexFilesDao.dirty() > 0) {
			    TimeUnit.SECONDS.sleep(60);
			}
			
			if (zookeeper != null) {
				ZKMessageUtil.dorefresh();
			    ZKLockUtil.unlockme(writelock);
			}
		    }
		}
		
		} catch (Exception e) {
			log.info(Constants.EXCEPTION, e);
		}

		List<List> retlistlist = new ArrayList<List>();
		retlistlist.add(delList);
		retlistlist.add(nonexistList);
		retlistlist.add(newList);
		return retlistlist;
	    }
    
}
