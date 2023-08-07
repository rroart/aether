package roart.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.collections.MySet;
import roart.common.collections.impl.MyAtomicLong;
import roart.common.collections.impl.MyAtomicLongs;
import roart.common.collections.impl.MyQueues;
import roart.common.collections.impl.MySets;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.OperationConstants;
import roart.common.filesystem.MyFile;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.util.FsUtil;
import roart.common.util.QueueUtil;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.dir.TraverseFile;
import roart.filesystem.FileSystemDao;
import roart.hcutil.GetHazelcastInstance;
import roart.queue.Queues;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;
import roart.util.TraverseUtil;
import roart.common.queue.QueueElement;

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
        Map<Future<Object>, Date> map = new HashMap<Future<Object>, Date>();
        int nThreads = ControlRunner.getThreads();
        nThreads = nodeConf.getMPThreadsFS();
        int running = 0;
        log.info("nthreads {}", nThreads);
        ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

        if (new Queues(nodeConf, controlService).getListings() > 0) {
            log.info("resetting listings");
            //Queues.resetListings();
        }

        while (true) {
            long now = System.currentTimeMillis();
            List<Future> removes = new ArrayList<Future>();
            for(Future<Object> task: map.keySet()) {
                if (task.isCancelled()) {
                    log.info("cancelled and removing " + task + " " + map.size());
                    removes.add(task);   
                    //FutureTask<Object> ft = (FutureTask<Object>) task;
                    //ft.run();
                    //ft.notify();
                    continue;
                }
                if (task.isDone()) {
                    log.info("removing " + task);
                    removes.add(task);                              
                    Date d = map.get(task);
                    if ( d != null) {
                        log.info("timerStop " + (now - d.getTime()));
                    }
                    continue;
                }
                Date d = map.get(task);
                if (true) { continue; }
                if ( d != null && (now - d.getTime()) < 100/*0*/ * 60 * 1/*0*/) {
                    continue;
                }

                //Queues.decListings();
                log.error("timeout and removing " + task + " " + map.size());
                boolean ok = task.cancel(true);
                if (!ok) {
                    log.error("canceled error");
                }
            }
            for (Future<Object> key: removes) {
                map.remove(key);
                running--;
                new Queues(nodeConf, controlService).decListings();
            }
            if (false && removes.size() > 0) {
                log.info("active 0 " + executorService.getActiveCount());
                executorService.purge();
                log.info("active 1 " + executorService.getActiveCount());
            }
            if (new Queues(nodeConf, controlService).getListingQueueSize() == 0 || new Queues(nodeConf, controlService).traverseQueueHeavyLoaded()) {
                if (new Queues(nodeConf, controlService).traverseQueueHeavyLoaded()) {
                    log.info("Traverse queue heavy loaded, sleeping");
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    log.error(Constants.EXCEPTION, e);
                }
                continue;
            }
            //Queues.queueStat();

            for(int i = running; i < nThreads; i++) {
                Callable<Object> callable = new Callable<Object>() {
                    public Object call() /* throws Exception*/ {
                        try {
                            doListingTimeout();
                        } catch (Exception e) {
                            log.error(Constants.EXCEPTION, e);
                        } catch (Error e) {
                            System.gc();
                            log.error("Error " + Thread.currentThread().getId());
                            log.error(Constants.ERROR, e);
                        }
                        finally {
                            //log.info("myend");
                        }
                        return null; //myMethod();
                    }       
                };      

                Future<Object> task = executorService.submit(callable);
                map.put(task, new Date());
                new Queues(nodeConf, controlService).queueStat();
                new Queues(nodeConf, controlService).incListings();
                running++;
                log.info("submit " + task + " " + running + " service count " + executorService.getActiveCount());
                log.info("queue " + executorService.getQueue());
                /*
                    int num = executorService.prestartAllCoreThreads();
                    log.info("num " + num);
                 */
            }
            try {
                //TimeUnit.SECONDS.sleep(60);
            } catch (/*Interrupted*/Exception e) {
                // TODO Auto-generated catch block
                log.error(Constants.EXCEPTION, e);
            }
        }
    }

    private void doListingTimeout() {
        int limit = nodeConf.getMPBatch();
        if (limit < 1) {
            limit = LIMIT;
        }
        MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getListingQueue();
        QueueElement listing = queue.poll(QueueElement.class);
        /*
        List<ListQueueElement> listingList = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            ListQueueElement trav = queue.poll();
            if (trav == null) {
                break;
            }
            if (!listingList.isEmpty() && !listingList.get(0).getFileObject().location.equals(trav.getFileObject().location)) {
                queue.offer(trav);
                break;
            }
            listingList.add(trav);
            FileObject filename = trav.getFileObject();
            log.debug("Listing file {}", filename);
        }
         */
        if (listing == null) {
            try {
                TimeUnit.SECONDS.sleep(1);
                return;
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e); 
            }                   
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
            return;
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
        List<MyFile> listDir = new FileSystemDao(nodeConf, controlService).listFilesFull(fileObject);
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
                MyQueue<String> notfoundset = (MyQueue<String>) MyQueues.get(QueueUtil.notfoundsetQueue(listing.getMyid()), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
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
                log.debug("isdir {}", filename);
                FileObject dirObject = file.fileObject[0];
                QueueElement listQueueElement = new QueueElement(listing.getMyid(), dirObject, listing.getClientQueueElement(), null);
                new Queues(nodeConf, controlService).getListingQueue().offer(listQueueElement);
                //Queues.getListingQueueSize().incrementAndGet();
                //retset.addAll(doList(fo));
            } else {
                MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
                QueueElement trav = new QueueElement(listing.getMyid(), fo, listing.getClientQueueElement(), null);
                TraverseUtil.doCounters(trav, 1, nodeConf, controlService);
                // save
                queue.offer(trav);
                MyQueue<String> filestodoset = (MyQueue<String>) MyQueues.get(QueueUtil.filestodoQueue(listing.getMyid()), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
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
        if (element.getOpid().equals(OperationConstants.LISTFILESFULL)) {
        //if (element.getFileSystemMyFileResult() == null) {
            FileObject fileObject = element.getFileObject();
            if (TraverseUtil.isMaxed(element.getMyid(), element.getClientQueueElement(), nodeConf, controlService)) {
                return;
            }

            if (TraverseUtil.indirlist(fileObject, dirlistnot)) {
                return;
            }
            //HashSet<String> md5set = new HashSet<String>();
            new FileSystemDao(nodeConf, controlService).listFilesFullQueue(element, fileObject);
        } else {
            Collection<MyFile> listDir = element.getFileSystemMyFileResult().map.values();
            element.setFileSystemMyFileResult(null);
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
                    MyQueue<String> notfoundset = (MyQueue<String>) MyQueues.get(QueueUtil.notfoundsetQueue(element.getMyid()), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
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
                    log.debug("isdir {}", filename);
                    FileObject dirObject = file.fileObject[0];
                    String queueName = QueueUtil.getListingQueue();
                    QueueElement listQueueElement = new QueueElement(element.getMyid(),dirObject,  element.getClientQueueElement(), queueName);
                    new Queues(nodeConf, controlService).getListingQueue().offer(listQueueElement);
                    //Queues.getListingQueueSize().incrementAndGet();
                    //retset.addAll(doList(fo));
                } else {
                    MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
                    QueueElement trav = new QueueElement(element.getMyid(), fo, element.getClientQueueElement(), null);
                    TraverseUtil.doCounters(trav, 1, nodeConf, controlService);
                    // save
                    queue.offer(trav);
                    MyQueue<String> filestodoset = (MyQueue<String>) MyQueues.get(QueueUtil.filestodoQueue(trav.getMyid()), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance()); 
                    filestodoset.offer(trav.getFileObject().toString());
                }
            }
        }
    }
}
