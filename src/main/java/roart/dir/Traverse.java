package roart.dir;

import java.util.Map;
import java.io.*;
import roart.content.*;
import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.queue.ClientQueueElement;
import roart.search.*;
import roart.thread.TikaRunner;
import roart.dao.IndexFilesDao;
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

    public static Set<String> doList(String dirname, Set<IndexFiles> newindexset, Set<String> newset, Map<String, HashSet<String>> dirset, String[] dirlistnot, boolean newmd5, boolean nomd5) throws Exception {
	Set<String> retset = new HashSet<String>();
	if (indirlistnot(dirname, dirlistnot)) {
	    return retset;
	}
	HashSet<String> md5set = new HashSet<String>();
	File dir = new File(dirname);
	File listDir[] = dir.listFiles();
	//log.info("dir " + dirname);
	//log.info("listDir " + listDir.length);
	if (listDir == null) {
	    return retset;
	}
	for (int i = 0; i < listDir.length; i++) {
	    String filename = listDir[i].getAbsolutePath();
	    if (filename.length() > MAXFILE) {
		log.info("Too large filesize " + filename);
		continue;
	    }
	    //log.info("file " + filename);
	    if (listDir[i].isDirectory()) {
		//log.info("isdir " + filename);
		retset.addAll(doList(filename, newindexset, newset, dirset, dirlistnot, newmd5, nomd5));
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
		if (nomd5 == false) {
		if (newmd5 == true || curMd5 == null) {
		    try {
			FileInputStream fis = new FileInputStream( new File(filename));
			String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex( fis );
			IndexFiles files = null;
			if (files == null) {
			    files = IndexFilesDao.getByMd5(md5);
			}
			files.addFile(filename);
			//IndexFilesDao.save(files);
			//IndexFilesDao.flush();
			log.info("adding md5 file " + filename);
			if (curMd5 == null || (newmd5 == true && !curMd5.equals(md5))) {
			    if (newset != null) {
				newset.add(filename);
			    }
			    if (newindexset != null) {
				newindexset.add(files);
			    }
			}
		    } catch (Exception e) {
			log.info("Error: " + e.getMessage());
			log.error("Exception", e);
		    }
		}
		}
		md5set.add(curMd5);
	    }
	}
	dirset.put(dirname, md5set);
	//log.info("retsize " + retset.size());
	return retset;
    }

    // retset will be returned empty
    // dirset will contain a map of directories, and the md5 files is contains
    // fileset will contain a map of md5 and the directories it has files in
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

    public static int indexnoFilter(ClientQueueElement el, IndexFiles index, boolean reindex, Set<IndexFiles> toindexset, Set<String> fileset, Set<String> md5set) throws Exception {
	String md5 = index.getMd5();
	String filename = getExistingFile(index);
	if (filename != null) {
	    toindexset.add(index);
	    md5set.add(md5);
	    fileset.add(filename);
	    return 1;
	}
	return 0;
    }

    public static int reindexsuffixFilter(ClientQueueElement el, IndexFiles index, String suffix, Set<IndexFiles> toindexset, Set<String> fileset, Set<String> md5set) throws Exception {
	String md5 = index.getMd5();
	for (FileLocation filename : index.getFilelocations()) {
	    if (suffix != null && !filename.getFilename().endsWith(suffix)) {
		continue;
	    }
	    File file = new File(filename.toString());
	    if (!file.exists()) {
		continue;
	    }
	    toindexset.add(index);
	    md5set.add(md5);
	    fileset.add(filename.toString());
	    return 1;
	}
	return 0;
    }

    public static List<List> indexnot(String add, boolean reindex) throws Exception {
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
		List<List> retlistlist2 = indexnot(filename, reindex);
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

    public static int reindexdateFilter(ClientQueueElement el, IndexFiles index, Set<IndexFiles> toindexset, Set<String> fileset, Set<String> md5set) throws Exception {
	String lowerdate = el.higherdate;
	String higherdate = el.higherdate;
	Long tslow = null;
	if (lowerdate != null) {
	    tslow = new Long(lowerdate);
	}
	Long tshigh = null;
	if (higherdate != null) {
	    tshigh = new Long(higherdate);
	}

	String timestamp = index.getTimestamp();
	if (timestamp != null) {
	    if (tslow != null && new Long(timestamp).compareTo(tslow) >= 0) {
		return 0;
	    }
	    if (tshigh != null && new Long(timestamp).compareTo(tshigh) <= 0) {
		return 0;
	    }
	}
	String md5 = index.getMd5();
	String filename = getExistingFile(index);

	if (filename == null) {
	    log.error("md5 filename null " + md5);
	    return 0;
	}

	toindexset.add(index);
	md5set.add(md5);
	fileset.add(filename);

	return 1;
    }

    public static void indexsingle(List<ResultItem> retlist, List<ResultItem> retlistnot, String md5, Map<String, Boolean> indexMap, Map<String, String> filesMapMd5, boolean reindex, int max) throws Exception {
	    if (md5 == null) {
		log.error("md5 should not be null");
		return;
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
	    index.setNoindexreason("");
	    TikaQueueElement e = new TikaQueueElement(filename, filename, md5, index, retlist, retlistnot, new Metadata());
	    Queues.tikaQueue.add(e);
	    //size = doTika(filename, filename, md5, index, retlist);
    }

    public static List<ResultItem> notindexed() throws Exception {
	List<ResultItem> retlist = new ArrayList<ResultItem>();
	ResultItem ri = new ResultItem();
	retlist.add(IndexFiles.getHeader());
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
	    ri = IndexFiles.getResultItem(index, "n/a");
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

    public static String getExistingFile(IndexFiles i) {
	// next up : locations
	Set<String> filenames = i.getFilenames();
	if (filenames == null) {
	    return null;
	}
	for (String filename : filenames) {
	    File file = new File(filename);
	    if (file.exists()) {
		return filename;
	    }
	}
	return null;
    }
    
}
