package roart.dir;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.collections.MySet;
import roart.common.collections.impl.MyAtomicLong;
import roart.common.collections.impl.MyAtomicLongs;
import roart.common.collections.impl.MyQueues;
import roart.common.collections.impl.MySets;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.filesystem.MyFile;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.inmemory.model.InmemoryUtil;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.common.synchronization.MyLock;
import roart.common.synchronization.MySemaphore;
import roart.common.synchronization.impl.MyLockFactory;
import roart.common.synchronization.impl.MySemaphoreFactory;
import roart.common.util.IOUtil;
import roart.database.IndexFilesDao;
import roart.filesystem.FileSystemDao;
import roart.function.AbstractFunction;
import roart.hcutil.GetHazelcastInstance;
import roart.queue.ConvertQueueElement;
import roart.queue.Queues;
import roart.queue.TraverseQueueElement;
import roart.search.SearchDao;
import roart.service.ControlService;
import roart.util.TraverseUtil;

public class TraverseFile {

    private static Logger log = LoggerFactory.getLogger(TraverseFile.class);

    private IndexFilesDao indexFilesDao;

    private NodeConfig nodeConf;

    private ControlService controlService;

    private SearchDao searchDao;

    public TraverseFile(IndexFilesDao indexFilesDao, NodeConfig nodeConf, ControlService controlService, SearchDao searchDao) {
        super();
        this.indexFilesDao = indexFilesDao;
        this.nodeConf = nodeConf;
        this.indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        this.controlService = controlService;
        this.searchDao = searchDao;
    }

    /**
     * Handle a single file during traversal
     * 
     * @param trav traverse queue element
     * @throws Exception
     */

    @Deprecated // ?
    public void handleFo3(TraverseQueueElement trav)
            throws Exception {
        //          if (controlService.zookeepersmall) {
        //              handleFo2(retset, md5set, filename);
        //          } else {
        // config with finegrained distrib
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        if (TraverseUtil.isMaxed(trav.getMyid(), trav.getClientQueueElement(), nodeConf, controlService)) {
            doCounters(trav, -1);
            return;
        }
        FileObject filename = trav.getFileobject();
        FileObject fo = new FileSystemDao(nodeConf, controlService).get(filename);

        //MyLock lock2 = MyLockFactory.create();
        //lock2.lock(fo.toString());
        log.debug("timer");
        String md5 = indexFilesDao.getMd5ByFilename(filename);
        log.debug("info {} {}", md5, filename);
        MySet<String> filestodoset = (MySet<String>) MySets.get(trav.getFilestodoid(), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
        if (!filestodoset.add(filename.toString())) {
            log.error("already added {}", filename);
        }
        IndexFiles files = null;
        MyLock lock = null; 
        boolean lockwait = false;
        if (trav.getClientQueueElement().md5checknew == true || md5 == null) {
            try {
                if (!new FileSystemDao(nodeConf, controlService).exists(fo)) {
                    throw new FileNotFoundException("File does not exist " + filename);
                }
                if (trav.getClientQueueElement().function != ServiceParam.Function.INDEX) {
                    try (InputStream fis = new FileSystemDao(nodeConf, controlService).getInputStream(fo)) {
                        md5 = DigestUtils.md5Hex( fis );
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                    }
                    if (files == null) {
                        //z.lock(md5);
                        // get read file
                        lock = MyLockFactory.create(md5, nodeConf.getLocker(), controlService.curatorClient, GetHazelcastInstance.instance());
                        lock.lock();
                        files = indexFilesDao.getByMd5(md5);
                    }
                    // modify write file
                    files.addFile(filename.location.toString(), filename.object);
                    indexFilesDao.addTemp(files);
                    log.info("adding md5 file {}", filename);
                }
                // calculatenewmd5 and nodbchange are never both true
                if (md5 == null || (trav.getClientQueueElement().md5checknew == true && !md5.equals(md5))) {
                    if (trav.getNewsetid() != null) {
                        MySet<String> newset = (MySet<String>) MySets.get(trav.getNewsetid(), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
                        newset.add(filename.toString());
                    }
                }
            } catch (FileNotFoundException e) {
                MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
                total.addAndGet(-1);
                MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid(), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
                count.addAndGet(-1);
                log.error(Constants.EXCEPTION, e);
                MySet<String> notfoundset = (MySet<String>) MySets.get(trav.getNotfoundsetid(), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
                notfoundset.add(filename.toString());
                log.debug("Count dec {}", trav.getFileobject());
                return;
            } catch (Exception e) {
                MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
                total.addAndGet(-1);
                MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid(), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
                count.addAndGet(-1);
                log.info("Error: {}", e.getMessage());
                log.error(Constants.EXCEPTION, e);
                log.debug("Count dec {}", trav.getFileobject());
                return;
            }
        } else {
            log.debug("timer2");
            // get read file
            lock = MyLockFactory.create(md5, nodeConf.getLocker(), controlService.curatorClient, GetHazelcastInstance.instance());
            lock.lock();
            files = indexFilesDao.getByMd5(md5);
            // TODO implement other wise
            // error case for handling when the supporting filename indexed
            // table has an entry, but no corresponding in the md5 indexed
            // table, and the files here just got created
            if (files.getFilelocations().isEmpty()) {
                log.error("existing file only");
                files.addFile(filename.location.toString(), filename.object);
                indexFilesDao.addTemp(files);
            }
            log.debug("info {} {}", md5, files);
        }
        if (files != null && lock != null) {
            files.setLock(lock);
            LinkedBlockingQueue lockqueue = new LinkedBlockingQueue();
            files.setLockqueue(lockqueue);
        }
        String md5sdoneid = "md5sdoneid"+trav.getMyid();
        // TODO mysets -> queue
        MySet<String> md5sdoneset = MySets.get(md5sdoneid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());

        try {
            if (md5 != null && md5sdoneset != null && !md5sdoneset.add(md5)) {
                return;
            }
            if (trav.getClientQueueElement().function == ServiceParam.Function.FILESYSTEM) {
                return;
            }
            if (true /*!Traverse.filterindex(files, trav)*/) {
                return;
            }
            lockwait = true;
            indexsingle(trav, md5, filename, files);
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
            log.error(Constants.EXCEPTION, e);
        } finally {
            log.debug("hereend");
            if (lockwait) {
                // TODO better flag than this?
                LinkedBlockingQueue lockqueue2 = (LinkedBlockingQueue) files.getLockqueue();
                if (lockqueue2 != null) {
                    log.debug("waiting");
                    lockqueue2.take();
                    log.debug("done waiting");
                }
            }
            if (false && files != null) {
                MyLock unlock = files.getLock();
                if (unlock != null) {
                    files.setLock(null);
                    unlock.unlock();
                }
            }
            if (!filestodoset.remove(filename.toString())) {
                log.error("already removed {}", filename);
            }
            doCounters(trav, -1);
        }
        //md5set.add(md5);
    }

    public void handleFo(TraverseQueueElement trav, Map<FileObject, MyFile> fsMap, Map<FileObject, String> filenameMd5Map, Map<String, IndexFiles> ifMap, Map<FileObject, String> filenameNewMd5Map, Map<FileObject, String> contentMap, Queue<MyLock> locks, Queue<MySemaphore> semaphores)
            throws Exception {
        //          if (controlService.zookeepersmall) {
        //              handleFo2(retset, md5set, filename);
        //          } else {
        // config with finegrained distrib
        if (TraverseUtil.isMaxed(trav.getMyid(), trav.getClientQueueElement(), nodeConf, controlService)) {
            doCounters(trav, -1);
            return;
        }
        FileObject filename = trav.getFileobject();
        // TODO this is new lock
        // TODO trylock, if false, all is invalid, but which
        MySemaphore folock = MySemaphoreFactory.create(filename.toString(), nodeConf.getLocker(), controlService.curatorClient, GetHazelcastInstance.instance());
        boolean flocked = folock.tryLock();
        if (!flocked) {
            MyQueue<TraverseQueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
            /*
            MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
            total.addAndGet(1);
            MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
            count.addAndGet(1);
             */
            // save
            queue.offer(trav);
            //Queues.getTraverseQueueSize().incrementAndGet();
            log.info("Already locked: {}", filename.toString());
            return;
        }
        try {
            FileObject fo = fsMap.get(filename).fileObject[0];
        } catch (Exception e) {
            int jj = 0;
        }
        //MyLock lock2 = MyLockFactory.create();
        //lock2.lock(fo.toString());
        log.debug("timer");
        String md5 = filenameMd5Map.get(filename);
        log.debug("info {} {}", md5, filename);
        IndexFiles indexfiles = null;
        MySemaphore lock = null; 
        boolean lockwait = false;
        boolean created = false;
        if (trav.getClientQueueElement().md5checknew == true || md5 == null) {
            try {
                if (!fsMap.get(filename).exists) {
                    throw new FileNotFoundException("File does not exist " + filename);
                }
                String oldMd5 = md5;
                md5 = filenameNewMd5Map.get(filename);
                if (md5 == null) {
                    log.error("Md5 null");
                    throw new Exception("Md5 null");
                }
                if("37a6259cc0c1dae299a7866489dff0bd".equals(md5)) {
                    int jj = 0;
                    // d41d8cd98f00b204e9800998ecf8427e
                }

                //if (files == null) {
                //z.lock(md5);
                // get read file
                // todo lock file name

                lock = MySemaphoreFactory.create(md5, nodeConf.getLocker(), controlService.curatorClient, GetHazelcastInstance.instance());
                boolean locked = lock.tryLock();
                if (!locked) {
                    MyQueue<TraverseQueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
                    /*
                    MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
                    total.addAndGet(1);
                    MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
                    count.addAndGet(1);
                     */
                    folock.unlock();
                    // save
                    queue.offer(trav);
                    log.info("Already locked: {}", md5);
                    return;
                }
                
                if (trav.getClientQueueElement().md5checknew == true) {
                    if (oldMd5 != null && !md5.equals(oldMd5)) {
                        log.info("Changed md5 {} {} {}", filename, oldMd5, md5);
                        MySemaphore oldLock = MySemaphoreFactory.create(oldMd5, nodeConf.getLocker(), controlService.curatorClient, GetHazelcastInstance.instance());
                        boolean oldLocked = oldLock.tryLock();
                        if (!oldLocked) {
                            MyQueue<TraverseQueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
                            lock.unlock();
                            folock.unlock();
                            // save
                            queue.offer(trav);
                            log.info("Already locked: {}", oldMd5);
                            return;
                        }
                        IndexFiles oldindexfiles = ifMap.get(oldMd5);
                        // null check
                        if (oldindexfiles == null) {
                            int jj = 0;
                        }
                        oldindexfiles.removeFile(filename.location.toString(), filename.object);
                        if (!oldindexfiles.getFilelocations().isEmpty()) {
                            indexFilesDao.add(oldindexfiles);
                            //oldindexfiles.setFlock(folock);
                            oldindexfiles.setSemaphorelock(oldLock);
                            oldindexfiles.setLockqueue(locks);
                            oldindexfiles.setSemaphorelockqueue(semaphores);
                        } else {
                            indexFilesDao.delete(oldindexfiles);
                            searchDao.deleteme(oldindexfiles.getMd5());
                            oldLock.unlock();
                        }
                    } else {
                        // TODO break
                        //MyQueue<TraverseQueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
                        //lock.unlock();
                        //folock.unlock();

                    }
                }

                indexfiles = ifMap.get(md5);
                //}
                if (indexfiles == null) {
                    // todo sync problem
                    //files = indexFilesDao.getByTemp(md5);
                }
                if (indexfiles == null) {
                    // TODO batch
                    // TODO bieffect
                    // not need? indexfiles = indexFilesDao.getByMd5(md5);
                    indexfiles = new IndexFiles(md5);
                    indexfiles.setCreated("" + System.currentTimeMillis());
                    created = true;
                }
                if (indexfiles == null /* && md5 != null*/) {
                    // not used
                    indexfiles = new IndexFiles(md5);
                    indexfiles.setCreated("" + System.currentTimeMillis());             
                }
                log.debug("Files {}", indexfiles);
                indexfiles.setChecked("" + System.currentTimeMillis());
                // modify write file
                // todo duplicate add
                indexfiles.addFile(filename.location.toString(), filename.object);
                //indexFilesDao.addTemp(indexfiles);
                log.info("adding md5 file {}", filename);
                // calculatenewmd5 and nodbchange are never both true
                if (md5 == null || (trav.getClientQueueElement().md5checknew == true && !md5.equals(md5))) {
                    if (trav.getNewsetid() != null) {
                        MyQueue<String> newset = (MyQueue<String>) MyQueues.get(trav.getNewsetid(), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
                        newset.offer(filename.toString());
                    }
                }
            } catch (FileNotFoundException e) {
                doCounters(trav, -1);
                log.error(Constants.EXCEPTION, e);
                MyQueue<String> notfoundset = (MyQueue<String>) MyQueues.get(trav.getNotfoundsetid(), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
                notfoundset.offer(filename.toString());
                return;
            } catch (Exception e) {
                doCounters(trav, -1);
                log.info("Error: {}", e.getMessage());
                log.error(Constants.EXCEPTION, e);
                return;
            }
        } else {
            log.debug("timer2");
            // get read file
            lock = MySemaphoreFactory.create(md5, nodeConf.getLocker(), controlService.curatorClient, GetHazelcastInstance.instance());
            boolean locked = lock.tryLock();
            if (!locked) {
                MyQueue<TraverseQueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
                /*
                MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
                total.addAndGet(1);
                MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
                count.addAndGet(1);
                 */
                folock.unlock();
                // save
                queue.offer(trav);
                log.info("Already locked: {}", md5);
                return;
            }
            indexfiles = ifMap.get(md5);
            // TODO implement other wise
            // error case for handling when the supporting filename indexed
            // table has an entry, but no corresponding in the md5 indexed
            // table, and the files here just got created
            if (indexfiles.getFilelocations().isEmpty()) {
                log.error("existing file only");
                indexfiles.addFile(filename.location.toString(), filename.object);
                //indexFilesDao.addTemp(indexfiles);
            }
            log.debug("info {} {}", md5, indexfiles);
        }
        if (indexfiles != null && lock != null) {
            indexfiles.setSemaphoreflock(folock);
            indexfiles.setSemaphorelock(lock);
            indexfiles.setLockqueue(locks);
            indexfiles.setSemaphorelockqueue(semaphores);
        }
        //String md5sdoneid = "md5sdoneid"+trav.getMyid();
        //MySet<String> md5sdoneset = MySets.get(md5sdoneid);

        try {
            // TODO new criteria
            boolean doindex = getDoIndex(trav, indexfiles, null);
            lockwait = true;
            // TODO indexfiles is new, created
            doindex = doindex && created;
            if (doindex) {
                indexFilesDao.add(indexfiles);                
                indexsingle(trav, md5, filename, indexfiles);
            } else {
                indexFilesDao.add(indexfiles);                
            }
            log.info("Added {}", indexfiles);
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
            log.error(Constants.EXCEPTION, e);
        } finally {
            log.debug("hereend");
            if (false && lockwait) {
                // TODO better flag than this?
                // TODO not used
                LinkedBlockingQueue lockqueue2 = (LinkedBlockingQueue) indexfiles.getLockqueue();
                if (lockqueue2 != null) {
                    log.debug("waiting");
                    lockqueue2.take();
                    log.debug("done waiting");
                }
            }
            if (false && indexfiles != null) {
                // TODO not here
                MySemaphore unlock = indexfiles.getSemaphorelock();
                if (unlock != null) {
                    indexfiles.setLock(null);
                    log.debug("Files {}", indexfiles);
                    unlock.unlock();
                }
            }
            MyQueue<String> filesdoneset = (MyQueue<String>) MyQueues.get(trav.getFilesdoneid(), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
            filesdoneset.offer(filename.toString());
            doCounters(trav, -1);
        }
        //md5set.add(md5);
        // TODO this is new unlock
        // TODO not unlock here
        //folock.unlock();

    }

    public boolean getDoIndex(TraverseQueueElement trav, IndexFiles files, AbstractFunction function) throws Exception {
        boolean doindex = true;
        if (trav.getClientQueueElement().function == ServiceParam.Function.FILESYSTEM) {
            doindex = false;
        }
        if (function != null && !function.indexFilter(files, trav)) {
            doindex = false;
        }
        return doindex;
    }

    /**
     * Index the filename with id, represented by index internally in the db
     * 
     * @param trav a traverse queue element
     * @param md5 id
     * @param filename to be indexed
     * @param index db representation
     */

    public void indexsingle(TraverseQueueElement trav,
            String md5, FileObject filename, IndexFiles index) {
        int maxfailed = nodeConf.getFailedLimit();
        if (!trav.getClientQueueElement().reindex && maxfailed > 0) {
            int failed = index.getFailed();
            if (failed >= maxfailed) {
                log.info("failed too much for {}", md5);
                return;
            }
        }

        log.debug("index {} {}", md5, filename);
        //InputStream stream = null;
        // file modify still
        index.setTimeoutreason("");
        index.setFailedreason("");
        index.setNoindexreason("");
        //TikaQueueElement e = new TikaQueueElement(filename, filename, md5, index, trav.getRetlistid(), trav.getRetnotlistid(), new Metadata(), fsData);
        //Queues.tikaQueue.add(e);
        String content = null; //contentMap.get(filename);
        ConvertQueueElement e2 = new ConvertQueueElement(filename, md5, index, trav.getRetlistid(), trav.getRetnotlistid(), new HashMap<>(), null, content);
        new Queues(nodeConf, controlService).getConvertQueue().offer(e2);
        //Queues.getConvertQueueSize().incrementAndGet();
        //size = doTika(filename, filename, md5, index, retlist);
    }

    public Map<FileObject, String> readFiles(List<TraverseQueueElement> traverseList, Map<FileObject, MyFile> fsMap) {
        Set<FileObject> filenames = new HashSet<>();
        for (TraverseQueueElement trav : traverseList) {
            FileObject filename = trav.getFileobject();
            try {
                if (!fsMap.get(filename).exists) {
                    throw new FileNotFoundException("File does not exist " + filename);
                }
                filenames.add(filename);
            } catch (FileNotFoundException e) {
                log.error(Constants.EXCEPTION, e);
                log.debug("Count dec {}", trav.getFileobject());
            } catch (Exception e) {
                log.info("Error: {}", e.getMessage());
                log.error(Constants.EXCEPTION, e);
                log.debug("Count dec {}", trav.getFileobject());
            }
        }
        Map<FileObject, String> contentMap = new HashMap<>();
        if (filenames.isEmpty()) {
            return contentMap;
        }
        Map<FileObject, InmemoryMessage> messageMap = new FileSystemDao(nodeConf, controlService).readFile(filenames);
        for (Entry<FileObject, InmemoryMessage> entry : messageMap.entrySet()) {
            FileObject filename = entry.getKey();
            InmemoryMessage message = entry.getValue();
            Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
            String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArrayMax(inmemory.getInputStream(message)));
            inmemory.delete(message);
            contentMap.put(filename, content);
        }
        return contentMap;
    }

    public Map<FileObject, String> getMd5(List<TraverseQueueElement> traverseList, Map<FileObject, MyFile> fsMap, Map<FileObject, String> filenameMd5Map) {
        Set<FileObject> filenames = new HashSet<>();
        for (TraverseQueueElement trav : traverseList) {
            FileObject filename = trav.getFileobject();
            String md5 = filenameMd5Map.get(filename);
            if (trav.getClientQueueElement().md5checknew == true || md5 == null) {
                try {
                    if (!fsMap.get(filename).exists) {
                        throw new FileNotFoundException("File does not exist " + filename);
                    }
                    filenames.add(filename);
                } catch (FileNotFoundException e) {
                    log.error(Constants.EXCEPTION, e);
                    log.debug("Count dec {}", trav.getFileobject());
                } catch (Exception e) {
                    log.info("Error: {}", e.getMessage());
                    log.error(Constants.EXCEPTION, e);
                    log.debug("Count dec {}", trav.getFileobject());
                }
            }
        }
        if (filenames.isEmpty()) {
            return new HashMap<>();
        }
        return new FileSystemDao(nodeConf, controlService).getMd5(filenames);
    }

    private void doCounters(TraverseQueueElement trav, int value) {
        MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
        total.addAndGet(value);
        MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid(), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
        count.addAndGet(value);
        log.debug("Count {} {}", value, trav.getFileobject());
    }

}
