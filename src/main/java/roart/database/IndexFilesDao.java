package roart.database;

import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.model.IndexFiles;
import roart.model.FileLocation;
import roart.service.ControlService;
import roart.util.ConfigConstants;
import roart.util.Constants;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexFilesDao {

    private static Logger log = LoggerFactory.getLogger(IndexFilesDao.class);

    private static volatile ConcurrentMap<String, IndexFiles> all = new ConcurrentHashMap<String, IndexFiles>();

    private static IndexFilesAccess indexFiles = null;

    public static void instance(String type) {
	if (indexFiles == null) {
	    if (type.equals(ConfigConstants.HIBERNATE)) {
		indexFiles = new HibernateIndexFilesAccess();
	    }
	    if (type.equals(ConfigConstants.HBASE)) {
		indexFiles = new HbaseIndexFilesAccess();
	    }
	    if (type.equals(ConfigConstants.DATANUCLEUS)) {
		indexFiles = new DataNucleusIndexFilesAccess();
	    }
	}
    }

    public static IndexFiles getByMd5(String md5, boolean create) throws Exception {
	if (md5 == null) {
	    return null;
	}
	if (all.containsKey(md5)) {
	    return all.get(md5);
	}
	synchronized(IndexFilesDao.class) {
	IndexFiles i = indexFiles.getByMd5(md5);
	if (i == null && create) {
	    i = new IndexFiles(md5);
	}
	if (i != null) {
	all.put(md5, i);
	}
	return i;
	}
    }

    public static IndexFiles getByMd5(String md5) throws Exception {
    	return getByMd5(md5, true);
    }

    public static IndexFiles getExistingByMd5(String md5) throws Exception {
    	return getByMd5(md5, false);
    }

    public static Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
	if (md5 == null) {
	    return null;
	}
	synchronized(IndexFilesDao.class) {
	return indexFiles.getFilelocationsByMd5(md5);
	}
    }

    public static IndexFiles getByFilenameNot(String filename) throws Exception {
	String nodename = ControlService.nodename;
	FileLocation fl = new FileLocation(nodename, filename);
	synchronized(IndexFilesDao.class) {
	return indexFiles.getByFilelocation(fl);
	}
    }

    public static IndexFiles getByFilelocationNot(FileLocation fl) throws Exception {
	synchronized(IndexFilesDao.class) {
	return indexFiles.getByFilelocation(fl);
	}
    }

    public static String getMd5ByFilename(String filename) throws Exception {
	String nodename = ControlService.nodename;
	FileLocation fl = new FileLocation(nodename, filename);
	synchronized(IndexFilesDao.class) {
	return indexFiles.getMd5ByFilelocation(fl);
	}
    }

    public static List<IndexFiles> getAll() throws Exception {
	//all.clear();
    	Set<String> allKeys = all.keySet();
	synchronized(IndexFilesDao.class) {
	List<IndexFiles> iAll = indexFiles.getAll();
	for (IndexFiles i : iAll) {
		if (allKeys.contains(i.getMd5())) {
			//continue;
		}
	    all.put(i.getMd5(), i);
	}
	return iAll;
	}
    }

    public static Set<String> getAllMd5() throws Exception {
	synchronized(IndexFilesDao.class) {
	Set<String> md5All = indexFiles.getAllMd5();
	return md5All;
	}
    }

    public static Set<String> getLanguages() throws Exception {
	synchronized(IndexFilesDao.class) {
    	Set<String> languages = indexFiles.getLanguages();
    	return languages;
	}
        }

    /*
    public static IndexFiles ensureExistence(String md5) throws Exception {
	IndexFiles fi = getByMd5(md5);
	if (fi == null) {
	    indexFilesJpa.ensureExistence(md5);
	}
	return fi;
    }
    */

    public static IndexFiles ensureExistenceNot(FileLocation filename) throws Exception {
	/*
	IndexFiles fi = getByMd5(md5);
	if (fi == null) {
	    indexFilesJpa.ensureExistence(md5);
	}
	*/
	return null;
    }

    public static void save(IndexFiles i) {
	if (i.hasChanged()) {
		try {
		    synchronized(IndexFilesDao.class) {
			indexFiles.save(i);
		    }
		    log.info("saving " + i.getMd5());
	    	i.setUnchanged();
		} catch (Exception e) {
		    log.info("failed saving " + i.getMd5());	
		    log.error(Constants.EXCEPTION, e);
	    }
	} else {
	    //log.info("not saving " + i.getMd5());
	}
    }

    public static IndexFiles instanceNot(String md5) {
	IndexFiles i = all.get(md5);
	if (i == null) {
	    i = new IndexFiles(md5);
	    all.put(md5, i);
	}
	return i;
    }

    public static void commit() {
	close();
    }

    public static void close() {
	for (String k : all.keySet()) {
	    IndexFiles i = all.get(k);
	    IndexFilesDao.save(i);
	}
	//all.clear();
	try {
	    synchronized(IndexFilesDao.class) {
	indexFiles.close();
	    }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
    }
    }

    public static void flush() {
    	try {
	    synchronized(IndexFilesDao.class) {
	indexFiles.flush();
	    }
		} catch (Exception e) {
		    log.error(Constants.EXCEPTION, e);
	    }
    }

   public static String webstat() {
       int dirty1 = dirty();
       return "d " + dirty1;
    }

   public static int dirty() {
       int dirty1 = 0;
       for (String k : all.keySet()) {
	    //log.info("save try " + Thread.currentThread().getId() + " " + k);
	    IndexFiles i = all.get(k);
	    if (i.hasChanged()) {
		dirty1++;
	    }
       }
       return dirty1;
    }

}
