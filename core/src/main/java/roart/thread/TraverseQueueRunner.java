package roart.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.queue.QueueElement;
import roart.common.synchronization.MyLock;
import roart.common.synchronization.MySemaphore;
import roart.database.IndexFilesDao;
import roart.dir.TraverseFile;
import roart.queue.Queues;
import roart.search.SearchDao;
import roart.service.ControlService;

public class TraverseQueueRunner implements Runnable {

    static Logger log = LoggerFactory.getLogger(TraverseQueueRunner.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static final int LIMIT = 100;

    private IndexFilesDao indexFilesDao;

    private TraverseFile traverseFile; 

    private NodeConfig nodeConf;

    private ControlService controlService;

    private SearchDao searchDao;

    public TraverseQueueRunner(NodeConfig nodeConf, ControlService controlService, SearchDao searchDao) {
        super();
        this.nodeConf = nodeConf;
        this.traverseFile = new TraverseFile(indexFilesDao, nodeConf, controlService, searchDao);
        this.indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        this.controlService = controlService;
        this.searchDao = searchDao;
    }

    @SuppressWarnings("squid:S2189")
    public void run() {
        int nThreads = ControlRunner.getThreads();
        nThreads = nodeConf.getMPThreadsFS();
        int running = 0;
        log.info("nthreads {}", nThreads);

        nThreads = 1;
        nThreads = 3;
        for(int i = running; i < nThreads; i++) {
            Runnable run = () -> {
                try {
                    Queue<MyLock> locks = new ConcurrentLinkedQueue<>();
                    Queue<MySemaphore> semaphores = new ConcurrentLinkedQueue<>();
                    while (true) {
                        try {
                            unlock(locks);
                        } catch (Exception e) {
                            log.error(Constants.EXCEPTION, e); 
                        }                   
                        try {
                            unlockSemaphores(semaphores);
                        } catch (Exception e) {
                            log.error(Constants.EXCEPTION, e); 
                        }                   
                        if (new Queues(nodeConf, controlService).getTraverseQueueSize() == 0) {
                            log.debug("Traverse queue empty, sleeping");
                            try {
                                TimeUnit.SECONDS.sleep(10);
                            } catch (InterruptedException e) {
                                log.error(Constants.EXCEPTION, e);
                            }
                            continue;
                        }
                        if (new Queues(nodeConf, controlService).convertQueueHeavyLoaded()) {
                            log.info("Convert queue heavy loaded, sleeping");
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                log.error(Constants.EXCEPTION, e);
                            }
                            continue;
                        }
                        new Queues(nodeConf, controlService).incTraverses();
                        doTraverseTimeout(locks, semaphores);
                        new Queues(nodeConf, controlService).decTraverses();
                    }
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
                return; //myMethod();
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

    private void doTraverseTimeout(Queue<MyLock> locks, Queue<MySemaphore> semaphores) {
        int limit = nodeConf.getMPBatch();
        if (limit < 1) {
            limit = LIMIT;
        }
        if (nodeConf.wantAsync()) {
            limit = 1;
        }
        MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
        List<QueueElement> traverseList = new ArrayList<>();
        // limit = 1;
        for (int i = 0; i < limit; i++) {
            QueueElement trav = queue.poll(QueueElement.class);
            if (trav == null) {
                break;
            }
            if (!traverseList.isEmpty() && !traverseList.get(0).getFileObject().location.equals(trav.getFileObject().location)) {
                queue.offer(trav);
                break;
            }
            //Queues.getTraverseQueueSize().decrementAndGet();
            traverseList.add(trav);
            FileObject filename = trav.getFileObject();
            log.debug("Traverse file {}", filename);
        }
        if (traverseList.isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
                return;
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e); 
            }                   
        }
        log.debug("Traverse list size {}", traverseList.size());

        try {
            //handleList(pool, traverseList);
            //handleList2(traverseList);
            if (/*false &&*/ nodeConf.wantAsync()) {
                handleListQueue3(traverseList.get(0), locks, semaphores);
            } else {
                handleList3(traverseList, locks, semaphores);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            for (QueueElement trav : traverseList) {
                queue.offer(trav);
            }
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

    private void unlock(Queue<MyLock> locks) {
        if (!locks.isEmpty()) {
            log.info("unlock");
        }
        while (!locks.isEmpty()) {
            MyLock lock = locks.poll();
            lock.unlock();
            if (locks.isEmpty()) {
                log.info("unlock");
            }
        }
    }

    private void unlockSemaphores(Queue<MySemaphore> locks) {
        if (!locks.isEmpty()) {
            log.info("unlock");
        }
        while (!locks.isEmpty()) {
            MySemaphore lock = locks.poll();
            lock.unlock();
            if (locks.isEmpty()) {
                log.info("unlock");
            }
        }
    }

    /**
     * 
     * Traverse list of max limit
     * 
     * @param traverseList
     * @param locks 
     * @param semaphores TODO
     * @throws Exception
     */
    
    private void handleList3(List<QueueElement> traverseList, Queue<MyLock> locks, Queue<MySemaphore> semaphores) throws Exception {
        Set<FileObject> filenames = new HashSet<>();
        // Create filenames set
        for (QueueElement trav : traverseList) {
            filenames.add(trav.getFileObject());
        }
        long time0 = System.currentTimeMillis();
        long time1 = System.currentTimeMillis();
        // Get Md5s by fileobject from database
        // TODO check if need full indexfiles?
        // TODO file may be gone after list
        Map<FileObject, String> filenameMd5Map = indexFilesDao.getMd5ByFilename(filenames);
        // Batch read md5, if have none or wants to calculate new
        Map<FileObject, String> filenameNewMd5Map = traverseFile.getMd5(traverseList, filenameMd5Map);
        long time2 = System.currentTimeMillis();
        Map<String, IndexFiles> ifOldMap = indexFilesDao.getByMd5(new HashSet<>(filenameMd5Map.values().stream().filter(e -> e != null).collect(Collectors.toList())));
        // with side effect
        Map<String, IndexFiles> ifNewMap = indexFilesDao.getByMd5(new HashSet<>(filenameNewMd5Map.values().stream().filter(e -> e != null).collect(Collectors.toList())), false);
        Map<String, IndexFiles> ifMap = new HashMap<>(ifOldMap);
        ifMap.putAll(ifNewMap);
        // Get IndexFiles by Md5 from database
        long time3 = System.currentTimeMillis();
        // Do individual traverse, index etc
        for (QueueElement trav : traverseList) {
            traverseFile.handleFo(trav, filenameMd5Map, ifMap, filenameNewMd5Map, locks, semaphores);
        }
        long time4 = System.currentTimeMillis();
        log.info("Times {} {} {} {}", usedTime(time1, time0), usedTime(time2, time1), usedTime(time3, time2), usedTime(time4, time3));
    }

    private void handleListQueue3(QueueElement traverseElement, Queue<MyLock> locks, Queue<MySemaphore> semaphores) throws Exception {
        traverseFile.handleFoQueue(traverseElement, locks, semaphores);
    }

    private int usedTime(long time2, long time1) {
        return (int) (time2 - time1); // 1000;
    }

}


