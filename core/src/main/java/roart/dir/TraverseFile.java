package roart.dir;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import roart.queue.ConvertQueueElement;
import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;
import roart.service.SearchService;
import roart.thread.TikaRunner;
import roart.common.collections.MySet;
import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.filesystem.MyFile;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.inmemory.model.InmemoryUtil;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.model.SearchDisplay;
import roart.common.service.ServiceParam;
import roart.common.service.ServiceParam.Function;
import roart.common.synchronization.MyLock;
import roart.common.util.ExecCommand;
import roart.common.util.IOUtil;
import roart.database.IndexFilesDao;
import roart.filesystem.FileSystemDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import roart.util.MyAtomicLong;
import roart.util.MyAtomicLongs;
import roart.util.MyLockFactory;
import roart.util.MySets;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.tika.metadata.Metadata;


public class TraverseFile {

    private static Logger log = LoggerFactory.getLogger(TraverseFile.class);

    /**
     * Handle a single file during traversal
     * 
     * @param trav traverse queue element
     * @throws Exception
     */
    
    public static void handleFo3(TraverseQueueElement trav)
            throws Exception {
        //          if (ControlService.zookeepersmall) {
        //              handleFo2(retset, md5set, filename);
        //          } else {
        // config with finegrained distrib
        if (Traverse.isMaxed(trav.getMyid(), trav.getClientQueueElement())) {
            MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
            total.addAndGet(-1);
            MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
            count.addAndGet(-1);
            log.debug("Count dec {}", trav.getFileobject());
            return;
        }
        FileObject filename = trav.getFileobject();
        FileObject fo = FileSystemDao.get(filename);

        //MyLock lock2 = MyLockFactory.create();
        //lock2.lock(fo.toString());
        log.debug("timer");
        String md5 = IndexFilesDao.getMd5ByFilename(filename);
        log.debug("info {} {}", md5, filename);
        MySet<String> filestodoset = (MySet<String>) MySets.get(trav.getFilestodoid()); 
        if (!filestodoset.add(filename.toString())) {
            log.error("already added {}", filename);
        }
        IndexFiles files = null;
        MyLock lock = null; 
        boolean lockwait = false;
        if (trav.getClientQueueElement().md5change == true || md5 == null) {
            try {
                if (!FileSystemDao.exists(fo)) {
                    throw new FileNotFoundException("File does not exist " + filename);
                }
                if (trav.getClientQueueElement().function != ServiceParam.Function.INDEX) {
                    InputStream fis = FileSystemDao.getInputStream(fo);
                    md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex( fis );
                    fis.close();
                    if (files == null) {
                        //z.lock(md5);
                        // get read file
                        lock = MyLockFactory.create();
                        lock.lock(md5);
                        files = IndexFilesDao.getByMd5(md5);
                    }
                    // modify write file
                    String nodename = ControlService.nodename;
                    files.addFile(filename.location.toString(), filename.object);
                    IndexFilesDao.addTemp(files);
                    log.info("adding md5 file {}", filename);
                }
                // calculatenewmd5 and nodbchange are never both true
                if (md5 == null || (trav.getClientQueueElement().md5change == true && !md5.equals(md5))) {
                    if (trav.getNewsetid() != null) {
                        MySet<String> newset = (MySet<String>) MySets.get(trav.getNewsetid()); 
                        newset.add(filename.toString());
                    }
                }
            } catch (FileNotFoundException e) {
                MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
                total.addAndGet(-1);
                MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
                count.addAndGet(-1);
                log.error(Constants.EXCEPTION, e);
                MySet<String> notfoundset = (MySet<String>) MySets.get(trav.getNotfoundsetid()); 
                notfoundset.add(filename.toString());
                log.debug("Count dec {}", trav.getFileobject());
                return;
            } catch (Exception e) {
                MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
                total.addAndGet(-1);
                MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
                count.addAndGet(-1);
                log.info("Error: {}", e.getMessage());
                log.error(Constants.EXCEPTION, e);
                log.debug("Count dec {}", trav.getFileobject());
                return;
            }
        } else {
            log.debug("timer2");
            // get read file
            lock = MyLockFactory.create();
            lock.lock(md5);
            files = IndexFilesDao.getByMd5(md5);
            // TODO implement other wise
            // error case for handling when the supporting filename indexed
            // table has an entry, but no corresponding in the md5 indexed
            // table, and the files here just got created
            if (files.getFilelocations().isEmpty()) {
                log.error("existing file only");
                String nodename = ControlService.nodename;
                files.addFile(filename.location.toString(), filename.object);
                IndexFilesDao.addTemp(files);
            }
            log.debug("info {} {}", md5, files);
        }
        if (files != null && lock != null) {
            files.setLock(lock);
            LinkedBlockingQueue lockqueue = new LinkedBlockingQueue();
            files.setLockqueue(lockqueue);
        }
        String md5sdoneid = "md5sdoneid"+trav.getMyid();
        MySet<String> md5sdoneset = MySets.get(md5sdoneid);

        try {
            if (md5 != null && md5sdoneset != null && !md5sdoneset.add(md5)) {
                return;
            }
            if (trav.getClientQueueElement().function == ServiceParam.Function.FILESYSTEM) {
                return;
            }
            if (!Traverse.filterindex(files, trav)) {
                return;
            }
            lockwait = true;
            indexsingle(trav, md5, filename, files, null, null);
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
            if (files != null) {
                MyLock unlock = files.getLock();
                if (unlock != null) {
                    files.setLock(null);
                    unlock.unlock();
                }
            }
            if (!filestodoset.remove(filename.toString())) {
                log.error("already removed {}", filename);
            }
            MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
            total.addAndGet(-1);
            MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
            count.addAndGet(-1);
            log.debug("Count dec {}", trav.getFileobject());
        }
        //md5set.add(md5);
    }

    public static void handleFo3(TraverseQueueElement trav, Map<FileObject, MyFile> fsMap, Map<FileObject, String> md5Map, Map<String, IndexFiles> ifMap, Map<FileObject, String> newMd5Map, Map<FileObject, String> contentMap)
            throws Exception {
        //          if (ControlService.zookeepersmall) {
        //              handleFo2(retset, md5set, filename);
        //          } else {
        // config with finegrained distrib
        if (Traverse.isMaxed(trav.getMyid(), trav.getClientQueueElement())) {
            MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
            total.addAndGet(-1);
            MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
            count.addAndGet(-1);
            log.debug("Count dec {}", trav.getFileobject());
            return;
        }
        FileObject filename = trav.getFileobject();
        try {
        FileObject fo = fsMap.get(filename).fileObject[0];
        } catch (Exception e) {
            int jj = 0;
        }
        //MyLock lock2 = MyLockFactory.create();
        //lock2.lock(fo.toString());
        log.debug("timer");
        String md5 = md5Map.get(filename);
        log.debug("info {} {}", md5, filename);
        MySet<String> filestodoset = (MySet<String>) MySets.get(trav.getFilestodoid()); 
        if (!filestodoset.add(filename.toString())) {
            log.error("already added {}", filename);
        }
        IndexFiles files = null;
        MyLock lock = null; 
        boolean lockwait = false;
        if (trav.getClientQueueElement().md5change == true || md5 == null) {
            try {
                if (!fsMap.get(filename).exists) {
                    throw new FileNotFoundException("File does not exist " + filename);
                }
                if (trav.getClientQueueElement().function != ServiceParam.Function.INDEX) {
		    md5 = newMd5Map.get(filename);
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
                        lock = MyLockFactory.create();
                        lock.lock(md5);
                        files = ifMap.get(md5);
                    //}
                        if (files == null) {
                            files = IndexFilesDao.getByTemp(md5);
                        }
                        if (files == null) {
                            files = IndexFilesDao.getByMd5(md5);
                        }
                        if (files == null) {
                            files = IndexFilesDao.getNewByMd5(md5);
                            //files = new IndexFiles(md5);
                        }
		    log.debug("Files {}", files);
		    files.setChecked("" + System.currentTimeMillis());
                    // modify write file
                    String nodename = ControlService.nodename;
                    files.addFile(filename.location.toString(), filename.object);
                    IndexFilesDao.addTemp(files);
                    log.info("adding md5 file {}", filename);
                }
                // calculatenewmd5 and nodbchange are never both true
                if (md5 == null || (trav.getClientQueueElement().md5change == true && !md5.equals(md5))) {
                    if (trav.getNewsetid() != null) {
                        MySet<String> newset = (MySet<String>) MySets.get(trav.getNewsetid()); 
                        newset.add(filename.toString());
                    }
                }
            } catch (FileNotFoundException e) {
                MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
                total.addAndGet(-1);
                MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
                count.addAndGet(-1);
                log.error(Constants.EXCEPTION, e);
                MySet<String> notfoundset = (MySet<String>) MySets.get(trav.getNotfoundsetid()); 
                notfoundset.add(filename.toString());
                log.debug("Count dec {}", trav.getFileobject());
                return;
            } catch (Exception e) {
                MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
                total.addAndGet(-1);
                MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
                count.addAndGet(-1);
                log.info("Error: {}", e.getMessage());
                log.error(Constants.EXCEPTION, e);
                log.debug("Count dec {}", trav.getFileobject());
                return;
            }
        } else {
            log.debug("timer2");
            // get read file
            lock = MyLockFactory.create();
            lock.lock(md5);
            files = ifMap.get(md5);
            // TODO implement other wise
            // error case for handling when the supporting filename indexed
            // table has an entry, but no corresponding in the md5 indexed
            // table, and the files here just got created
            if (files.getFilelocations().isEmpty()) {
                log.error("existing file only");
                String nodename = ControlService.nodename;
                files.addFile(filename.location.toString(), filename.object);
                IndexFilesDao.addTemp(files);
            }
            log.debug("info {} {}", md5, files);
        }
        if (files != null && lock != null) {
            files.setLock(lock);
            LinkedBlockingQueue lockqueue = new LinkedBlockingQueue();
            files.setLockqueue(lockqueue);
        }
        String md5sdoneid = "md5sdoneid"+trav.getMyid();
        MySet<String> md5sdoneset = MySets.get(md5sdoneid);

        try {
            boolean doindex = true;
            if (md5 != null && md5sdoneset != null && !md5sdoneset.add(md5)) {
                /*
                MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
                total.addAndGet(-1);
                MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
                count.addAndGet(-1);
                log.info("Count dec {}", trav.getFileobject());
                */
                doindex = false;
            }
            if (trav.getClientQueueElement().function == ServiceParam.Function.FILESYSTEM) {
                doindex = false;
            }
            if (!Traverse.filterindex(files, trav)) {
                doindex = false;
            }
            lockwait = true;
            if (doindex) {
            indexsingle(trav, md5, filename, files, fsMap, contentMap);
            }
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
            if (files != null) {
                MyLock unlock = files.getLock();
                if (unlock != null) {
                    files.setLock(null);
		    log.debug("Files {}", files);
                    unlock.unlock();
                }
            }
            if (!filestodoset.remove(filename.toString())) {
                log.error("already removed {}", filename);
            }
            MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
            total.addAndGet(-1);
            MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
            count.addAndGet(-1);
            log.debug("Count dec {}", trav.getFileobject());
        }
        //md5set.add(md5);
    }

    public static void handleFo3(TraverseQueueElement trav, Set<FileObject> filenames)
            throws Exception {
        FileObject filename = trav.getFileobject();
        // ADD
        // fo=fsdao.get(fn) fsdao.exists(fo) fsdao.getfis()
        // md5=ifdao.getmd5(fn) ifdao.getbymd5(md5)
        filenames.add(filename);
        //Map<String, List> lists = FileSystemDao.get(filenames);
        //Map<String, List> lists2 = IndexFilesDao.get(filenames);
    }

    /**
     * Index the filename with id, represented by index internally in the db
     * 
     * @param trav a traverse queue element
     * @param md5 id
     * @param filename to be indexed
     * @param index db representation
     * @param contentMap TODO
     */
    
    public static void indexsingle(TraverseQueueElement trav,
            String md5, FileObject filename, IndexFiles index, Map<FileObject, MyFile> fsMap, Map<FileObject, String> contentMap) {
        int maxfailed = MyConfig.conf.getFailedLimit();
        if (!trav.getClientQueueElement().reindex && maxfailed > 0) {
            int failed = index.getFailed();
            if (failed >= maxfailed) {
                log.info("failed too much for {}", md5);
                return;
            }
        }

        log.info("index {} {}", md5, filename);
        //InputStream stream = null;
        // file modify still
        index.setTimeoutreason("");
        index.setFailedreason("");
        index.setNoindexreason("");
        MyFile fsData = null;
        if (fsMap != null) {
            fsData = fsMap.get(filename);
        }
        //TikaQueueElement e = new TikaQueueElement(filename, filename, md5, index, trav.getRetlistid(), trav.getRetnotlistid(), new Metadata(), fsData);
        //Queues.tikaQueue.add(e);
        String content = contentMap.get(filename);
        ConvertQueueElement e2 = new ConvertQueueElement(filename, filename, md5, index, trav.getRetlistid(), trav.getRetnotlistid(), new HashMap<>(), fsData, null, content);
        Queues.convertQueue.add(e2);
        //size = doTika(filename, filename, md5, index, retlist);
    }

    public static Map<FileObject, MyFile> handleFo3(Set<FileObject> filenames) {
        Map<FileObject, MyFile> result = FileSystemDao.getWithoutInputStream(filenames);
        return result;
    }

    public static Map<FileObject, String> handleFo4(Set<FileObject> filenames) throws Exception {
        Map<FileObject, String> result = IndexFilesDao.getMd5ByFilename(filenames);
        return result;
    }

    public static Map<String, IndexFiles> handleFo5(Set<String> md5s) throws Exception {
        Map<String, IndexFiles> result = IndexFilesDao.getByMd5(md5s);
        return result;
    }

    public static Map<FileObject, String> readFiles(List<TraverseQueueElement> traverseList, Map<FileObject, MyFile> fsMap) {
        Set<FileObject> filenames = new HashSet<>();
        for (TraverseQueueElement trav : traverseList) {
            FileObject filename = trav.getFileobject();
            try {
                if (!fsMap.get(filename).exists) {
                    throw new FileNotFoundException("File does not exist " + filename);
                }
                if (trav.getClientQueueElement().function == ServiceParam.Function.INDEX) {
                    filenames.add(filename);
                }
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
        Map<FileObject, InmemoryMessage> messageMap = FileSystemDao.readFile(filenames);
        for (Entry<FileObject, InmemoryMessage> entry : messageMap.entrySet()) {
            FileObject filename = entry.getKey();
            InmemoryMessage message = entry.getValue();
            Inmemory inmemory = InmemoryFactory.get(MyConfig.conf.getInmemoryServer(), MyConfig.conf.getInmemoryHazelcast(), MyConfig.conf.getInmemoryRedis());
            String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArrayMax(inmemory.getInputStream(message)));
            inmemory.delete(message);
            contentMap.put(filename, content);
        }
        return contentMap;
    }
    
    public static Map<FileObject, String> getMd5(List<TraverseQueueElement> traverseList, Map<FileObject, MyFile> fsMap, Map<FileObject, String> md5Map) {
        Set<FileObject> filenames = new HashSet<>();
        for (TraverseQueueElement trav : traverseList) {
            FileObject filename = trav.getFileobject();
            String md5 = md5Map.get(filename);
            if (trav.getClientQueueElement().md5change == true || md5 == null) {
                try {
                    if (!fsMap.get(filename).exists) {
                        throw new FileNotFoundException("File does not exist " + filename);
                    }
                    if (trav.getClientQueueElement().function != ServiceParam.Function.INDEX) {
                        filenames.add(filename);
                    }
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
        return FileSystemDao.getMd5(filenames);
    }
}
