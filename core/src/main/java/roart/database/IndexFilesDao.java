package roart.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.Files;
import roart.common.model.IndexFiles;
import roart.common.synchronization.MyLock;
import roart.service.ControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexFilesDao {

    private static Logger log = LoggerFactory.getLogger(IndexFilesDao.class);

    private static volatile ConcurrentMap<String, IndexFiles> all = new ConcurrentHashMap<String, IndexFiles>();

    private static volatile ConcurrentMap<String, IndexFiles> dbi = new ConcurrentHashMap<String, IndexFiles>();

    private static volatile ConcurrentMap<String, IndexFiles> dbitemp = new ConcurrentHashMap<String, IndexFiles>();

    private static IndexFilesAccess indexFiles = null;

    public static synchronized void instance(String type) {
        if (indexFiles == null) {
            if (type.equals(ConfigConstants.DATABASEHIBERNATE)) {
                indexFiles = new HibernateIndexFilesAccess();
            }
            if (type.equals(ConfigConstants.DATABASEHBASE)) {
                indexFiles = new HbaseIndexFilesAccess();
            }
            if (type.equals(ConfigConstants.DATABASECASSANDRA)) {
                indexFiles = new CassandraIndexFilesAccess();
            }
            if (type.equals(ConfigConstants.DATABASEDYNAMODB)) {
                indexFiles = new DynamodbIndexFilesAccess();
            }
            if (type.equals(ConfigConstants.DATABASEDATANUCLEUS)) {
                indexFiles = new DataNucleusIndexFilesAccess();
            }
        }
    }

    // with zookeepersmall, lock must be held when entering here

    public static IndexFiles getByMd5(String md5, boolean create) throws Exception {
        if (md5 == null) {
            return null;
        }
        if (false && !MyConfig.conf.wantZookeeperSmall()) {
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
            i.setCreated("" + System.currentTimeMillis());
            return i;
        }
    }

    public static IndexFiles getByMd5(String md5) throws Exception {
        return getByMd5(md5, true);
    }

    public static IndexFiles getNewByMd5(String md5) throws Exception {
        if (md5 == null) {
            return null;
        }
        synchronized(IndexFilesDao.class) {
            IndexFiles i = new IndexFiles(md5);
            if (i != null) {
                all.put(md5, i);
            }
            i.setCreated("" + System.currentTimeMillis());
            return i;
        }
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

    public static IndexFiles getByFilelocationNot(FileLocation fl) throws Exception {
        synchronized(IndexFilesDao.class) {
            return indexFiles.getByFilelocation(fl);
        }
    }

    public static String getMd5ByFilename(FileObject filename) throws Exception {
        FileLocation fl = new FileLocation(filename.location.toString(), filename.object);
        synchronized(IndexFilesDao.class) {
            return indexFiles.getMd5ByFilelocation(fl);
        }
    }

    public List<IndexFiles> getAll() throws Exception {
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

    public List<Files> getAllFiles() throws Exception {
        //all.clear();
        Set<String> allKeys = all.keySet();
        synchronized(IndexFilesDao.class) {
            List<Files> iAll = indexFiles.getAllFiles();
            for (Files i : iAll) {
                if (allKeys.contains(i.getMd5())) {
                    //continue;
                }
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

    public static void save(Set<IndexFiles> saves, IndexFiles i) {
        if (i.hasChanged()) {
            try {
                synchronized(IndexFilesDao.class) {
                    boolean exist = !saves.add(i);
                    if (exist) {
                        log.error("Already double " + i.getPriority() + " " + i.getMd5());
                    }
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

    public void add(IndexFiles i) {
        synchronized(IndexFilesDao.class) {
        dbi.putIfAbsent(i.getMd5(), i);
        }
    }

    public static void addTemp(IndexFiles i) {
        synchronized(IndexFilesDao.class) {
        dbitemp.putIfAbsent(i.getMd5(), i);
        }
    }

    public static IndexFiles getByTemp(String md5) {
        synchronized(IndexFilesDao.class) {
        return dbitemp.remove(md5);
        }
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
        synchronized(IndexFilesDao.class) {
        int[] pris = getPris();
        log.info("dbis {}", dbi.keySet());
        log.info("dbitemps {}", dbitemp.keySet());
        log.info("pris levels {} {}", pris[0], pris[1]);
        if (pris[0] > 0) {
            log.info("saving finished");
        }
        Set<IndexFiles> saves = new HashSet<>();
        for (Entry<String, IndexFiles> entry : dbi.entrySet()) {
            String key = entry.getKey();
            IndexFiles i = entry.getValue();
            IndexFilesDao.save(saves, i);
            MyLock lock = i.getLock();
            if (lock != null) {
                LinkedBlockingQueue lockqueue = (LinkedBlockingQueue) i.getLockqueue();
                if (lockqueue != null) {
                    lockqueue.offer(lock);
                } else {
                    log.error("lockqueue null for {}", i.getMd5());
                }
            } else {
                log.error("lock null for {}", i.getMd5());
            }
            dbi.remove(key);
            dbitemp.remove(key);
        }
        if (pris[1] > 0) {
            log.info("saving temporarily");
        }
        for (Entry<String, IndexFiles> entry : dbitemp.entrySet()) {
            IndexFiles i = entry.getValue();
            IndexFilesDao.save(saves, i);
            dbitemp.remove(entry.getKey());
        }
        //all.clear();
        try {
            synchronized(IndexFilesDao.class) {
                indexFiles.save(saves);
                indexFiles.commit();
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        }
    }

    private static int[] getPris() {
        int[] pris = { dbi.size(), dbitemp.size() };
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
        int[] pris = getPris();
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

    public void delete(IndexFiles index) {
        try {
            synchronized(IndexFilesDao.class) {
                indexFiles.delete(index);
            }
            all.remove(index.getMd5());
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    public static Map<String, IndexFiles> getByMd5(Set<String> md5s) throws Exception {
        return getByMd5(md5s, true);
    }

    public static Map<String, IndexFiles> getByMd5(Set<String> md5s, boolean create) throws Exception {
        if (md5s == null) {
            return null;
        }
        Map<String, IndexFiles> indexFileMap = new HashMap<>();
        if (false && !MyConfig.conf.wantZookeeperSmall()) {
            for (String md5 : md5s) {
                if (all.containsKey(md5)) {
                    indexFileMap.put(md5, all.get(md5));
                }
            }
        }
        synchronized(IndexFilesDao.class) {
            Map<String, IndexFiles> is = indexFiles.getByMd5(md5s);
            for (String md5 : md5s) {
                IndexFiles i = is.get(md5);
                if (i == null && create) {
                    i = new IndexFiles(md5);
                }
                if (i != null) {
                    all.put(md5, i);
                }
            }
            return is;
        }
    }

    public static Map<FileObject, String> getMd5ByFilename(Set<FileObject> filenames) throws Exception {
        Set<FileLocation> fls = new HashSet<>();
        for (FileObject filename : filenames) {
            FileLocation fl = new FileLocation(filename.location.toString(), filename.object);
            fls.add(fl);
            if (false && "::".equals(filename.location.toString())) {
                fls.add(new FileLocation(null, "file:" + filename.object));
                fls.add(new FileLocation(null, "file://localhost" + filename.object));
                fls.add(new FileLocation(null, "localhost:" + filename.object));
            }
        }
        synchronized(IndexFilesDao.class) {
            Map<String, String> map = indexFiles.getMd5ByFilelocation(fls);
            Map<FileObject, String> retMap = new HashMap<>();
            for (Entry<String, String> entry : map.entrySet()) {
                retMap.put(new FileObject(filenames.iterator().next().location, entry.getKey()), entry.getValue());
            }
            return retMap;
        }
    }

    public static List<Map> getBothByFilename(Set<String> filenames) throws Exception {
        String nodename = ControlService.nodename;
        Set<FileLocation> fls = new HashSet<>();
        for (String filename : filenames) {
            FileLocation fl = new FileLocation(nodename, filename);
            fls.add(fl);
        }
        synchronized(IndexFilesDao.class) {
            return indexFiles.getBothByFilelocation(fls);
        }
    }

    public static void clear() {
        indexFiles.clear();
    }

    public static void drop() {
        indexFiles.drop();
    }

}
