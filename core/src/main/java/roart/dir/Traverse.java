package roart.dir;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import roart.queue.ListQueueElement;
import roart.queue.Queues;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;
import roart.service.SearchService;
import roart.common.collections.MyQueue;
import roart.common.collections.MySet;
import roart.common.collections.impl.MyAtomicLong;
import roart.common.collections.impl.MyAtomicLongs;
import roart.common.collections.impl.MyQueues;
import roart.common.collections.impl.MySets;
import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.filesystem.MyFile;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.SearchDisplay;
import roart.common.service.ServiceParam;
import roart.common.synchronization.MyLock;
import roart.common.synchronization.impl.MyLockFactory;
import roart.common.util.ExecCommand;
import roart.common.util.FsUtil;
import roart.database.IndexFilesDao;
import roart.filesystem.FileSystemDao;
import roart.function.AbstractFunction;
import roart.hcutil.GetHazelcastInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.HashMap;

import roart.util.TraverseUtil;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

public class Traverse {

    private static Logger log = LoggerFactory.getLogger(Traverse.class);

    public static final int MAXFILE = 500;

    private IndexFilesDao indexFilesDao;

    //int max = 0;
    //int indexcount = 0;

    String myid;
    ServiceParam element;
    String retlistid = null;
    String retnotlistid = null;
    //List<ResultItem> retList = null;
    //List<ResultItem> retNotList = null;
    String newsetid = null; 
    //  MySet<String> newset = null; 
    //  Map<String, HashSet<String>> dirset;
    String notfoundsetid;
    //boolean reindex = false;
    //boolean calculatenewmd5;
    String filestodosetid;
    String traversecountid;
    boolean nomd5;
    String filesdonesetid;

    FileObject[] dirlistnot;
    SearchDisplay display;
    NodeConfig nodeConf;
    ControlService controlService;
    //Set<String> md5sdone = new HashSet<String>();

    public Traverse(String myid, ServiceParam element, String retlistid, String retnotlistid, String newsetid, String[] dirlistnotarr, String notfoundsetid, String filestodosetid, String traversecountid, boolean nomd5, String filesdonesetid, NodeConfig nodeConf, ControlService controlService) {

        this.myid = myid;
        this.element = element;
        this.retlistid = retlistid;
        this.retnotlistid = retnotlistid;
        this.newsetid = newsetid;
        this.notfoundsetid = notfoundsetid;
        //this.reindex = reindex;
        //this.calculatenewmd5 = newmd5;
        this.filestodosetid = filestodosetid;
        this.traversecountid = traversecountid;
        this.nomd5 = nomd5;
        this.filesdonesetid = filesdonesetid;
        this.nodeConf = nodeConf;
        this.indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        this.controlService = controlService;
        dirlistnotarr = nodeConf.getDirListNot();
        dirlistnot = new FileObject[dirlistnotarr.length];
        int i = 0;
        for (String dir : dirlistnotarr) {
            dirlistnot[i++] = FsUtil.getFileObject(dir);
        }
        //UI ui = element.ui;
        //this.display = SearchService.getSearchDisplay(ui);
    }

    // cases:
    // INDEX
    // reindex true, path null, not used
    // reindex true, path non-null, reindex given path
    // reindex false, path null, for indexing all unindexed found in all fs (eventually from db)
    // reindex false, path non-null, for indexing all unindexed found in given path 
    // REINDEXLANGUAGE
    // reindex true
    // path null
    // on lang
    // (eventually from db) 
    // REINDEXSUFFIX
    // reindex true or false
    // path null
    // on suffix
    // (eventually from db) 
    // REINDEXDATE
    // reindex true
    // path null
    // on date
    // (eventually from db) 
    // FILESYSTEM
    // check all fs for new if null path
    // check gives fs for new with path
    // FILESYSTEMLUCENENEW
    // check all fs for new if null path
    // check gives fs for new with path, eventually also compute new md5
    // then index if new

    // TODO concurrent
    public Set<String> doList(FileObject fileObject) throws Exception {
        Set<String> retset = new HashSet<>();
        if (TraverseUtil.isMaxed(myid, element, nodeConf, controlService)) {
            return retset;
        }

        if (TraverseUtil.indirlistnot(fileObject, dirlistnot)) {
            return retset;
        }
        //HashSet<String> md5set = new HashSet<String>();
        long time0 = System.currentTimeMillis();
        FileObject dir = new FileSystemDao(nodeConf, controlService).get(fileObject);
        List<MyFile> listDir = new FileSystemDao(nodeConf, controlService).listFilesFull(dir);
        long time1 = System.currentTimeMillis();
        log.debug("Time0 {}", usedTime(time1, time0));
        //log.info("dir " + dirname);
        //log.info("listDir " + listDir.length);
        if (listDir == null) {
            return retset;
        }
        for (MyFile file : listDir) {
            FileObject fo = file.fileObject[0];
            long time2 = System.currentTimeMillis();
            String filename = file.absolutePath;
            // for encoding problems
            if (!file.exists) {
                MyQueue<String> notfoundset = (MyQueue<String>) MyQueues.get(notfoundsetid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
                notfoundset.offer(filename);
                continue;
                //throw new FileNotFoundException("File does not exist " + filename);
            }
            long time3 = System.currentTimeMillis();
            log.debug("Time2 {}", usedTime(time3, time2));
            if (filename.length() > MAXFILE) {
                log.info("Too large filesize {}", filename);
                continue;
            }
            //log.info("file " + filename);
            if (file.isDirectory) {
                log.debug("isdir {}", filename);
                retset.addAll(doList(fo));
            } else {
                retset.add(filename);
                if (!nomd5) {
                    MyQueue<TraverseQueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
                    TraverseQueueElement trav = new TraverseQueueElement(myid, fo, element, retlistid, retnotlistid, newsetid, notfoundsetid, filestodosetid, traversecountid, filesdonesetid);
                    MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
                    total.addAndGet(1);
                    MyAtomicLong count = MyAtomicLongs.get(traversecountid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
                    count.addAndGet(1);
                    // save
                    queue.offer(trav);
                    //Queues.getTraverseQueueSize().incrementAndGet();
                    log.debug("Count inc {}", trav.getFileobject());
                    //TraverseFile.handleFo3(null, fo);
                }
            }
        }
        /*
        if (dirset != null) {
            dirset.put(dirname, md5set);
        }
         */
        //log.info("retsize " + retset.size());
        return retset;
    }

    public String getExistingLocalFile(IndexFiles i) {
        FileLocation fl = null;
        try {
            fl = TraverseUtil.getExistingLocalFilelocation(i, nodeConf, controlService);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        if (fl != null) {
            return fl.getFilename();
        }
        return null;
    }

    public Set<String> traversedb(AbstractFunction function, String add) throws Exception {
        MyQueue<TraverseQueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
        indexFilesDao.getAllFiles();
        List<IndexFiles> indexes = indexFilesDao.getAll();
        for (IndexFiles index : indexes) {
            if (TraverseUtil.isMaxed(myid, element, nodeConf, controlService)) {
                break;
            }
            String md5 = index.getMd5();
            //String name = getExistingLocalFile(index);
            FileLocation fl = index.getaFilelocation();
            FileObject filename = FsUtil.getFileObject(fl);
            /*
            if (name == null) {
                log.error("filename should not be null {}", md5);
                continue;
            }
            */
            // TODO check if fo needed
            TraverseQueueElement trav = new TraverseQueueElement(myid, filename, element, retlistid, retnotlistid, newsetid, notfoundsetid, filestodosetid, traversecountid, filesdonesetid);
            if (!function.indexFilter(index, trav)) {
                continue;
            }
            // config with finegrained distrib
            MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
            total.addAndGet(1);
            MyAtomicLong count = MyAtomicLongs.get(traversecountid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
            count.addAndGet(1);
            // ?
            //queue.offer(trav);
            //String md5sdoneid = "md5sdoneid"+trav.getMyid();
            //MySet<String> md5sdoneset = MySets.get(md5sdoneid);
            TraverseFile traverseFile = new TraverseFile(indexFilesDao, nodeConf, controlService);
            if (traverseFile.getDoIndex(trav, index, function)) {
                traverseFile.indexsingle(trav, md5, FsUtil.getFileObject(index.getaFilelocation()), index);
            }
            //MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
            total.addAndGet(-1);
            //MyAtomicLong count = MyAtomicLongs.get(trav.getTraversecountid());
            count.addAndGet(-1);
        }

        return null;
    }

    public Set<String> traverse(String add, AbstractFunction function) throws Exception {
        try {
            log.info("function: {}", element.function);
            if (add != null) {
                //return doList(FsUtil.getFileObject(add));
                FileObject fileObject = FsUtil.getFileObject(add);
                ListQueueElement listQueueElement = new ListQueueElement(fileObject, myid, element, retlistid, retnotlistid, newsetid, notfoundsetid, filestodosetid, traversecountid, nomd5, filesdonesetid);
                new Queues(nodeConf, controlService).getListingQueue().offer(listQueueElement);
                //Queues.getListingQueueSize().incrementAndGet();
            return new HashSet<>();
            } else {
                Set<String> retList = new HashSet<>();
                String[] dirlist = nodeConf.getDirList();
                for (int i = 0; i < dirlist.length; i ++) {
                    FileObject fileObject = FsUtil.getFileObject(dirlist[i]);
                    ListQueueElement listQueueElement = new ListQueueElement(fileObject, myid, element, retlistid, retnotlistid, newsetid, notfoundsetid, filestodosetid, traversecountid, nomd5, filesdonesetid);
                    new Queues(nodeConf, controlService).getListingQueue().offer(listQueueElement);
                    //Queues.getListingQueueSize().incrementAndGet();
                    //retList.addAll(doList(FsUtil.getFileObject(dirlist[i])));
                }
                return new HashSet<>();
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

    /**
     * Check whether fileobject is local
     * 
     * @param fo Fileobject to be checked
     * @return boolean state
     */

    // ?
    @Deprecated
    public boolean isLocal(FileObject fo) {
        return new FileSystemDao(nodeConf, controlService).exists(fo);
    }

    private int usedTime(long time2, long time1) {
        return (int) (time2 - time1); // / 1000;
    }
    
}
