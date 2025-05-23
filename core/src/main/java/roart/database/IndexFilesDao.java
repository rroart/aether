package roart.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import roart.common.collections.MyQueue;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.Files;
import roart.common.model.IndexFiles;
import roart.common.queue.QueueElement;
import roart.common.synchronization.MyLock;
import roart.common.synchronization.MyObjectLock;
import roart.common.synchronization.MyObjectLockData;
import roart.common.synchronization.MySemaphore;
import roart.common.synchronization.impl.MyObjectLockFactory;
import roart.service.ControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Queue;

public class IndexFilesDao {

    private static Logger log = LoggerFactory.getLogger(IndexFilesDao.class);

    private static volatile ConcurrentMap<String, IndexFiles> dbi = new ConcurrentHashMap<String, IndexFiles>();

    @Deprecated
    private static volatile ConcurrentMap<String, IndexFiles> dbitemp = new ConcurrentHashMap<String, IndexFiles>();

    private IndexFilesDS indexFiles = null;

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public IndexFilesDao(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.indexFiles = IndexFilesDSFactory.get(nodeConf, controlService);
        this.controlService = controlService;
    }

    // with zookeepersmall, lock must be held when entering here

    // todo get and create, bieffect
    public IndexFiles getByMd5(String md5, boolean create) throws Exception {
        if (md5 == null) {
            return null;
        }
        synchronized(IndexFilesDao.class) {
            IndexFiles i = indexFiles.getByMd5(md5);
            if (i == null && create) {
                i = new IndexFiles(md5);
            }
            i.setCreated("" + System.currentTimeMillis());
            return i;
        }
    }

    // todo get and create, bieffect
    public IndexFiles getByMd5(String md5) throws Exception {
        return getByMd5(md5, true);
    }

    // not used
    public IndexFiles getExistingByMd5(String md5) throws Exception {
        return getByMd5(md5, false);
    }

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
        if (md5 == null) {
            return null;
        }
        synchronized(IndexFilesDao.class) {
            return indexFiles.getFilelocationsByMd5(md5);
        }
    }

    public IndexFiles getByFilelocationNot(FileLocation fl) throws Exception {
        synchronized(IndexFilesDao.class) {
            return indexFiles.getByFilelocation(fl);
        }
    }

    // TODO
    public String getMd5ByFilename(FileObject filename) throws Exception {
        FileLocation fl = new FileLocation(filename.location.toString(), filename.object);
        synchronized(IndexFilesDao.class) {
            return indexFiles.getMd5ByFilelocation(fl);
        }
    }

    public List<IndexFiles> getAll() throws Exception {
        synchronized(IndexFilesDao.class) {
            List<IndexFiles> iAll = indexFiles.getAll();
            return iAll;
        }
    }

    public List<Files> getAllFiles() throws Exception {
        synchronized(IndexFilesDao.class) {
            List<Files> iAll = indexFiles.getAllFiles();
            return iAll;
        }
    }

    // not used
    public Set<String> getAllMd5() throws Exception {
        synchronized(IndexFilesDao.class) {
            Set<String> md5All = indexFiles.getAllMd5();
            return md5All;
        }
    }

    public Set<String> getLanguages() throws Exception {
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

    @Deprecated
    public IndexFiles ensureExistenceNot(FileLocation filename) throws Exception {
        /*
	IndexFiles fi = getByMd5(md5);
	if (fi == null) {
	    indexFilesJpa.ensureExistence(md5);
	}
         */
        return null;
    }

    public void save(Set<IndexFiles> saves, IndexFiles i) {
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

    public void add(IndexFiles i) {
        synchronized(IndexFilesDao.class) {
            IndexFiles prev = dbi.putIfAbsent(i.getMd5(), i);
            if (prev != null) {
                log.error("Already present {}", prev.getMd5());
            }
        }
    }

    public void addTemp(IndexFiles i) {
        synchronized(IndexFilesDao.class) {
        dbitemp.putIfAbsent(i.getMd5(), i);
        }
    }

    public IndexFiles getByTemp(String md5) {
        synchronized(IndexFilesDao.class) {
        return dbitemp.remove(md5);
        }
    }

    public void close() {
        try {
            synchronized(IndexFilesDao.class) {
                indexFiles.close();
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    public void commit() {
        synchronized(IndexFilesDao.class) {
        int[] pris = getPris();
        if (!dbi.isEmpty()) {
            log.info("dbis {}", dbi.keySet());
        }
        log.debug("dbitemps {}", dbitemp.keySet());
        log.debug("pris levels {} {}", pris[0], pris[1]);
        if (pris[0] > 0) {
            log.info("saving finished");
        }
        Set<IndexFiles> saves = new HashSet<>();
        for (Entry<String, IndexFiles> entry : dbi.entrySet()) {
            String key = entry.getKey();
            IndexFiles i = entry.getValue();
            save(saves, i);
            MyLock lock = i.getLock();
            if (false && lock != null) {
                // TODO not used
                LinkedBlockingQueue lockqueue = (LinkedBlockingQueue) i.getLockqueue();
                if (lockqueue != null) {
                    lockqueue.offer(lock);
                } else {
                    log.error("lockqueue null for {}", i.getMd5());
                }
            } else {
                //log.error("lock null for {}", i.getMd5());
            }
            //dbi.remove(key);
            dbitemp.remove(key);
        }
        if (pris[1] > 0) {
            log.info("saving temporarily");
        }
        for (Entry<String, IndexFiles> entry : dbitemp.entrySet()) {
            IndexFiles i = entry.getValue();
            save(saves, i);
            dbitemp.remove(entry.getKey());
        }
        try {
            synchronized(IndexFilesDao.class) {
                if (!saves.isEmpty()) {
                    indexFiles.save(saves);
                    indexFiles.commit();
                }
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        for (Entry<String, IndexFiles> entry : dbi.entrySet()) {
            String key = entry.getKey();
            IndexFiles i = entry.getValue();
            dbi.remove(key);
            Queue<MyLock> queue = (Queue<MyLock>) i.getLockqueue();
            if (i.getLock() != null) {
                 queue.offer(i.getLock());
            }
            if (i.getFlock() != null) {
                queue.offer(i.getFlock());
            }
            Queue<MySemaphore> semaphoreQueue = (Queue<MySemaphore>) i.getSemaphorelockqueue();
            if (i.getSemaphorelock() != null) {
                semaphoreQueue.offer(i.getSemaphorelock());
           }
           if (i.getSemaphoreflock() != null) {
               semaphoreQueue.offer(i.getSemaphoreflock());
           }
           if (i.getObjectlock() != null) {
               MyObjectLockData lockdata = i.getObjectlock();
               if (lockdata != null) {
                   MyObjectLock lock = MyObjectLockFactory.create(lockdata.id, nodeConf.getLocker(), controlService.curatorClient);
                   lock.unlock();
               }
               i.setObjectlock(null);
           }
           if (i.getObjectflock() != null) {
               MyObjectLockData flockdata = i.getObjectflock();
               if (flockdata != null) {
                   MyObjectLock lock = MyObjectLockFactory.create(flockdata.id, nodeConf.getLocker(), controlService.curatorClient);
                   lock.unlock();
               }
               i.setObjectflock(null);
           }
        }
        }
        // todo unlock
    }

    private int[] getPris() {
        int[] pris = { dbi.size(), dbitemp.size() };
        return pris;
    }

    public void flush() {
        try {
            synchronized(IndexFilesDao.class) {
                indexFiles.flush();
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    public String webstat() {
        int[] pris = getPris();
        return "d " + pris[0] + " / " + pris[1];
    }

    public int dirty() {
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
                indexFiles.commit();
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    public void delete(Files index) {
        try {
            synchronized(IndexFilesDao.class) {
                indexFiles.delete(index);
                indexFiles.commit();
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    // todo has bieffect
    public Map<String, IndexFiles> getByMd5(Set<String> md5s) throws Exception {
        return getByMd5(md5s, true);
    }

    // todo has bieffect
    public Map<String, IndexFiles> getByMd5(Set<String> md5s, boolean create) throws Exception {
        if (md5s == null) {
            return null;
        }
        synchronized(IndexFilesDao.class) {
            Map<String, IndexFiles> is = indexFiles.getByMd5(md5s);
            for (String md5 : md5s) {
                IndexFiles i = is.get(md5);
                if (i == null && create) {
                    i = new IndexFiles(md5);
                }
            }
            return is;
        }
    }

    // TODO
    public Map<FileObject, String> getMd5ByFilename(Set<FileObject> filenames) throws Exception {
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

    // not used
    public List<Map> getBothByFilename(Set<String> filenames) throws Exception {
        String nodename = controlService.nodename;
        Set<FileLocation> fls = new HashSet<>();
        for (String filename : filenames) {
            FileLocation fl = new FileLocation(nodename, filename);
            fls.add(fl);
        }
        synchronized(IndexFilesDao.class) {
            return indexFiles.getBothByFilelocation(fls);
        }
    }

    public void clear() {
        indexFiles.clear();
    }

    public void drop() {
        indexFiles.drop();
    }

    public void sendDelete(IndexFiles indexFiles) {
        //new MyQueueFactory().create(name, nodeConf, curatorFramework, hz);
        //MyQueue<T> queue = null;
        //queue.offer(indexFiles);
    }

    public void getByMd5Queue(QueueElement element, Set<String> md5s) throws Exception {
        if (md5s == null) {
            return;
        }
        synchronized(IndexFilesDao.class) {
            indexFiles.getByMd5Queue(element, md5s);
        }
    }

    // TODO
    public void getMd5ByFilenameQueue(QueueElement element, Set<FileObject> filenames) throws Exception {
        Set<FileLocation> fls = new HashSet<>();
        for (FileObject filename : filenames) {
            FileLocation fl = new FileLocation(filename.location.toString(), filename.object);
            fls.add(fl);
        }
        synchronized(IndexFilesDao.class) {
            indexFiles.getMd5ByFilelocationQueue(element, fls);
        }
    }

    public MyQueue<QueueElement> getQueue() {
        return indexFiles.getQueue(null);
    }
}
