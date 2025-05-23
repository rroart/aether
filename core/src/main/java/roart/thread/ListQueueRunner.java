package roart.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.collections.MySet;
import roart.common.collections.impl.MyQueues;
import roart.common.collections.impl.MySets;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.OperationConstants;
import roart.common.filesystem.MyFile;
import roart.common.model.FileObject;
import roart.common.queue.QueueElement;
import roart.common.service.ServiceParam;
import roart.common.util.FsUtil;
import roart.common.util.QueueUtil;
import roart.common.util.TimeUtil;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.filesystem.FileSystemDao;
import roart.queue.Queues;
import roart.service.ControlService;
import roart.util.FilterUtil;
import roart.util.TraverseUtil;

public class ListQueueRunner implements Runnable {
    static Logger log = LoggerFactory.getLogger(ListQueueRunner.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static final int LIMIT = 100;

    private IndexFilesDao indexFilesDao;

    ThreadPoolExecutor /*ExecutorService*/ pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    private FileObject[] dirlistnot;

    private NodeConfig nodeConf;

    private ControlService controlService;

    public ListQueueRunner(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        this.controlService = controlService;
    }

    @SuppressWarnings("squid:S2189")
    public void run() {
        getdirlistnot();
        int nThreads = ControlRunner.getThreads();
        nThreads = nodeConf.getMPThreadsFS();
        int running = 0;
        log.info("nthreads {}", nThreads);

        for(int i = running; i < nThreads; i++) {
            Runnable run = () -> {
                while (true) {
                    try {
                        if (new Queues(nodeConf, controlService).getListingQueueSize() == 0) {
                            log.debug("Listing queue empty, sleeping");
                            TimeUtil.sleep(10);
                            continue;
                        }
                        if (false) {
                        if (new Queues(nodeConf, controlService).listingQueueHeavyLoaded()) {
                            log.info("List queue heavy loaded, sleeping");
                            TimeUtil.sleep(1);
                            continue;
                        }
                        }
                        if (new Queues(nodeConf, controlService).filesystemQueueHeavyLoaded()) {
                            log.info("Filesystem queue heavy loaded, sleeping");
                            TimeUtil.sleep(1);
                            continue;
                        }
                        if (new Queues(nodeConf, controlService).traverseQueueHeavyLoaded()) {
                            log.info("Traverse queue heavy loaded, sleeping");
                            TimeUtil.sleep(1);
                            continue;
                        }
                        new Queues(nodeConf, controlService).incListings();
                        doListingTimeout();
                        new Queues(nodeConf, controlService).decListings();
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                        TimeUtil.sleep(10);
                    }
                }
            };      
            new Thread(run).start();
        }
        try {
            TimeUnit.DAYS.sleep(1000);
            return;
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e); 
        }                   
    }
 
    private void doListingTimeout() {
        MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getListingQueue();
        QueueElement listing = null;
        try {
            listing = queue.poll(QueueElement.class);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e); 
        }
        if (listing == null) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e); 
            }                   
            return;
        }
        //Queues.getListingQueueSize().decrementAndGet();
        //log.debug("Listing list size {}", listingList.size());

        try {
            //handleList(pool, listingList);
            //handleList2(listingList);
            handleList3(pool, listing);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        } catch (Error e) {
            System.gc();
            log.error("Error " + Thread.currentThread().getId());
            log.error(Constants.ERROR, e);
        }
        finally {
            log.debug("myend");            
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e); 
        }                   
    }

    private int usedTime(long time2, long time1) {
        return (int) (time2 - time1); // 1000;
    }

    private void handleList3(ThreadPoolExecutor pool, QueueElement listing) throws Exception {
        Runnable runnable = new MyRunnable3(listing);
        pool.execute(runnable);
    }

    class MyRunnable3 implements Runnable {
        QueueElement listing;

        public MyRunnable3(QueueElement listing2) {
            super();
            this.listing = listing2;
        }

        @Override
        public void run() {
            try {
                if (nodeConf.wantAsync()) {
                    doListQueue(listing);
                } else {
                    doList(listing);
                }
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }

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

    public void doList(QueueElement listing) throws Exception {
        FileObject fileObject = listing.getFileObject();
        if (TraverseUtil.isMaxed(listing.getMyid(), listing.getClientQueueElement(), nodeConf, controlService)) {
            return;
        }

        if (TraverseUtil.indirlist(fileObject, dirlistnot)) {
            return;
        }
        //HashSet<String> md5set = new HashSet<String>();
        long time0 = System.currentTimeMillis();
        
        String traversedsetid = QueueUtil.traversedSet("");
        MySet<String> traversedSet = MySets.get(traversedsetid, nodeConf, controlService.curatorClient);

        Collection<MyFile> listDir = new FileSystemDao(nodeConf, controlService).listFilesFull(fileObject);
        listDir = shuffle(listDir);
        long time1 = System.currentTimeMillis();
        log.debug("Time0 {}", usedTime(time1, time0));
        //log.info("dir " + dirname);
        //log.info("listDir " + listDir.length);
        if (listDir == null) {
            return;
        }
        for (MyFile file : listDir) {
            FileObject fo = file.fileObject[0];
            long time2 = System.currentTimeMillis();
            String filename = file.absolutePath;
            // for encoding problems
            if (!file.exists) {
                MyQueue<String> notfoundset = (MyQueue<String>) MyQueues.get(QueueUtil.notfoundsetQueue(listing.getMyid()), nodeConf, controlService.curatorClient); 
                notfoundset.offer(filename);
                continue;
                //throw new FileNotFoundException("File does not exist " + filename);
            }
            long time3 = System.currentTimeMillis();
            log.debug("Time2 {}", usedTime(time3, time2));
            if (filename.length() > Traverse.MAXFILE) {
                log.info("Too large filesize {}", filename);
                continue;
            }
            //log.info("file " + filename);
            if (file.isDirectory) {
                while (new Queues(nodeConf, controlService).listingQueueHeavyLoaded()) {
                    log.info("List queue heavy loaded, sleeping");
                    TimeUtil.sleep(1);
                }

                while (new Queues(nodeConf, controlService).traverseQueueHeavyLoaded()) {
                    log.debug("Traverse queue heavy loaded, sleeping");
                    TimeUtil.sleep(1);
                }
                log.debug("isdir {}", filename);
                FileObject dirObject = file.fileObject[0];
                QueueElement listQueueElement = new QueueElement(listing.getMyid(), dirObject, listing.getClientQueueElement(), null);
                new Queues(nodeConf, controlService).getListingQueue().offer(listQueueElement);
                //Queues.getListingQueueSize().incrementAndGet();
                //retset.addAll(doList(fo));
            } else {
                if (!FilterUtil.filterSuffix(fo, listing.getClientQueueElement())) {
                    continue;
                }
                if (traversedSet.remove(fo.toString())) {
                    log.info("Done already {}", fo.toString());
                    continue;
                }
                while (new Queues(nodeConf, controlService).listingQueueHeavyLoaded()) {
                    log.info("List queue heavy loaded, sleeping");
                    TimeUtil.sleep(1);
                }

                while (new Queues(nodeConf, controlService).traverseQueueHeavyLoaded()) {
                    log.debug("Traverse queue heavy loaded, sleeping");
                    TimeUtil.sleep(1);
                }
                MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
                QueueElement trav = new QueueElement(listing.getMyid(), fo, listing.getClientQueueElement(), null);
                TraverseUtil.doCounters(trav, 1, nodeConf, controlService);
                // save
                queue.offer(trav);
                MyQueue<String> filestodoset = (MyQueue<String>) MyQueues.get(QueueUtil.filestodoQueue(listing.getMyid()), nodeConf, controlService.curatorClient); 
                filestodoset.offer(trav.getFileObject().toString());
            }
        }
    }

    public void getdirlistnot() {
        String[] dirlistnotarr = nodeConf.getDirListNot();
        dirlistnot = new FileObject[dirlistnotarr.length];
        int i = 0;
        for (String dir : dirlistnotarr) {
            dirlistnot[i++] = FsUtil.getFileObject(dir);
        }

    }

    public void doListQueue(QueueElement element) throws Exception {
        if (!(element.getOpid() != null && element.getOpid().equals(OperationConstants.LISTFILESFULL))) {
        //if (element.getFileSystemMyFileResult() == null) {
            FileObject fileObject = element.getFileObject();
            if (TraverseUtil.isMaxed(element.getMyid(), element.getClientQueueElement(), nodeConf, controlService)) {
                return;
            }

            if (TraverseUtil.indirlist(fileObject, dirlistnot)) {
                return;
            }
            //HashSet<String> md5set = new HashSet<String>();
            // TODO no need? element.setOpid(null);
            element.setQueue(QueueUtil.getListingQueue());
            new FileSystemDao(nodeConf, controlService).listFilesFullQueue(element, fileObject);
        } else {
            String traversedsetid = QueueUtil.traversedSet("");
            MySet<String> traversedSet = MySets.get(traversedsetid, nodeConf, controlService.curatorClient);
            
            Collection<MyFile> listDir = element.getFileSystemMyFileResult().map.values();
            listDir = shuffle(listDir);
            // TODO not needing? element.setFileSystemMyFileResult(null);
            //log.info("dir " + dirname);
            //log.info("listDir " + listDir.length);
            if (listDir == null) {
                return;
            }
            for (MyFile file : listDir) {
                FileObject fo = file.fileObject[0];
                long time2 = System.currentTimeMillis();
                String filename = file.absolutePath;
                // for encoding problems
                if (!file.exists) {
                    MyQueue<String> notfoundset = (MyQueue<String>) MyQueues.get(QueueUtil.notfoundsetQueue(element.getMyid()), nodeConf, controlService.curatorClient); 
                    notfoundset.offer(filename);
                    continue;
                    //throw new FileNotFoundException("File does not exist " + filename);
                }
                long time3 = System.currentTimeMillis();
                log.debug("Time2 {}", usedTime(time3, time2));
                if (filename.length() > Traverse.MAXFILE) {
                    log.info("Too large filesize {}", filename);
                    continue;
                }
                //log.info("file " + filename);
                if (file.isDirectory) {
                    while (new Queues(nodeConf, controlService).listingQueueHeavyLoaded()) {
                        log.info("List queue heavy loaded, sleeping");
                        TimeUtil.sleep(1);
                    }

                    while (new Queues(nodeConf, controlService).traverseQueueHeavyLoaded()) {
                        log.debug("Traverse queue heavy loaded, sleeping");
                        TimeUtil.sleep(1);
                    }
                    
                    log.debug("isdir {}", filename);
                    FileObject dirObject = file.fileObject[0];
                    String queueName = QueueUtil.getListingQueue();
                    QueueElement listQueueElement = new QueueElement(element.getMyid(),dirObject,  element.getClientQueueElement(), queueName);
                    new Queues(nodeConf, controlService).getListingQueue().offer(listQueueElement);
                    //Queues.getListingQueueSize().incrementAndGet();
                    //retset.addAll(doList(fo));
                } else {
                    if (!FilterUtil.filterSuffix(fo, element.getClientQueueElement())) {
                        continue;
                    }
                    // the same as contains boolean
                    if (traversedSet.remove(fo.toString())) {
                        log.info("Done already {}", fo.toString());
                        continue;
                    }
  
                    while (new Queues(nodeConf, controlService).listingQueueHeavyLoaded()) {
                        log.info("List queue heavy loaded, sleeping");
                        TimeUtil.sleep(1);
                    }

                    while (new Queues(nodeConf, controlService).traverseQueueHeavyLoaded()) {
                        log.debug("Traverse queue heavy loaded, sleeping");
                        TimeUtil.sleep(1);
                    }
                    
                    MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
                    QueueElement trav = new QueueElement(element.getMyid(), fo, element.getClientQueueElement(), null);
                    TraverseUtil.doCounters(trav, 1, nodeConf, controlService);
                    // save
                    queue.offer(trav);
                    MyQueue<String> filestodoset = (MyQueue<String>) MyQueues.get(QueueUtil.filestodoQueue(trav.getMyid()), nodeConf, controlService.curatorClient); 
                    filestodoset.offer(trav.getFileObject().toString());
                }
            }
        }
    }

    static Collection<MyFile> shuffle(Collection<MyFile> listDir) {
        List<MyFile> listDir2 = new ArrayList<>(listDir);
        Collections.shuffle(listDir2);
        listDir = listDir2;
        return listDir;
    }
}
