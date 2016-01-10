package roart.database;

import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import roart.config.ConfigConstants;
import roart.config.MyConfig;
import roart.model.IndexFiles;
import roart.model.FileLocation;
import roart.service.ControlService;
import roart.util.Constants;
import roart.util.MyLock;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexFilesDao {

    private static Logger log = LoggerFactory.getLogger(IndexFilesDao.class);

    private static volatile ConcurrentMap<String, IndexFiles> all = new ConcurrentHashMap<String, IndexFiles>();

    private static volatile ConcurrentMap<String, IndexFiles> dbi = new ConcurrentHashMap<String, IndexFiles>();

    private static volatile ConcurrentMap<String, IndexFiles> dbitemp = new ConcurrentHashMap<String, IndexFiles>();

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
    
 // with zookeepersmall, lock must be held when entering here
    
    public static IndexFiles getByMd5(String md5, boolean create) throws Exception {
	if (md5 == null) {
	    return null;
	}
	if (false && !MyConfig.conf.zookeepersmall) {
	if (all.containsKey(md5)) {
	    return all.get(md5);
	}
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
		    log.info("saving pri " + i.getPriority() + " " + i.getMd5());
	    	i.setUnchanged();
            i.setPriority(0);
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

    public static void add(IndexFiles i) {
        dbi.putIfAbsent(i.getMd5(), i);
    }
    
    public static void addTemp(IndexFiles i) {
        dbitemp.putIfAbsent(i.getMd5(), i);
    }
    
    public static void close() {
        try {
            synchronized(IndexFilesDao.class) {
        indexFiles.close();
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    public static void commit() {
        int[] pris = getPris();
        log.info("pris levels " + pris[0] + " " + pris[1]);
        if (pris[0] > 0) {
            log.info("saving finished");
        }
        for (String k : dbi.keySet()) {
            IndexFiles i = dbi.get(k);
            IndexFilesDao.save(i);
            MyLock lock = i.getLock();
            if (lock != null) {
                LinkedBlockingQueue lockqueue = (LinkedBlockingQueue) i.getLockqueue();
                if (lockqueue != null) {
                    lockqueue.offer(lock);
                } else {
                    log.error("lockqueue null for " + i.getMd5());
                }
            } else {
                log.error("lock null for "  + i.getMd5());
            }
            dbi.remove(k);
            dbitemp.remove(k);
        }
        if (pris[1] > 0) {
            log.info("saving temporarily");
        }
        for (String k : dbitemp.keySet()) {
            IndexFiles i = dbitemp.get(k);
            IndexFilesDao.save(i);
            dbitemp.remove(k);
        }
	//all.clear();
	try {
	    synchronized(IndexFilesDao.class) {
	indexFiles.commit();
	    }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
    }
    }

    private static int[] getPris() {
        int pris[] = { dbi.size(), dbitemp.size() };
        /*
        for (String k : all.keySet()) {
            IndexFiles i = all.get(k);
            int priority = i.getPriority();
            if (priority <= 1) {
                pris[priority]++;
            } else {
                log.error("priority " + priority);
            }
        }
        */
        return pris;
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
       int [] pris = getPris();
       return "d " + pris[0] + " / " + pris[1];
    }

   public static int dirty() {
       int [] pris = getPris();
       if (true) return pris[0] + pris[1];
       int dirty1 = 0;
       for (String k : dbi.keySet()) {
	    //log.info("save try " + Thread.currentThread().getId() + " " + k);
	    IndexFiles i = dbi.get(k);
	    if (i.hasChanged()) {
		dirty1++;
	    }
       }
       return dirty1;
    }

public static void delete(IndexFiles index) {
    try {
    synchronized(IndexFilesDao.class) {
indexFiles.delete(index);
    }
    all.remove(index);
    } catch (Exception e) {
        log.error(Constants.EXCEPTION, e);
    }
}

}
