package roart.thread;

import java.util.ArrayList;
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
import roart.common.collections.impl.MyQueues;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.filesystem.MyFile;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.synchronization.MyLock;
import roart.common.synchronization.MySemaphore;
import roart.database.IndexFilesDao;
import roart.dir.TraverseFile;
import roart.filesystem.FileSystemDao;
import roart.queue.Queues;
import roart.queue.TraverseQueueElement;
import roart.search.SearchDao;
import roart.service.ControlService;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TraverseQueueRunner implements Runnable {

    static Logger log = LoggerFactory.getLogger(TraverseQueueRunner.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static final int LIMIT = 100;

    private IndexFilesDao indexFilesDao;

    private TraverseFile traverseFile; 

    ThreadPoolExecutor /*ExecutorService*/ pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

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
        Map<Future<Object>, Date> map = new HashMap<Future<Object>, Date>();
        int nThreads = ControlRunner.getThreads();
        nThreads = nodeConf.getMPThreadsFS();
        int running = 0;
        log.info("nthreads {}", nThreads);
        ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

        if (new Queues(nodeConf, controlService).getTraverses() > 0) {
            log.info("resetting traverses");
            //Queues.resetTraverses();
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

                //Queues.decTraverses();
                log.error("timeout and removing " + task + " " + map.size());
                boolean ok = task.cancel(true);
                if (!ok) {
                    log.error("canceled error");
                }
            }
            for (Future<Object> key: removes) {
                map.remove(key);
                running--;
                new Queues(nodeConf, controlService).decTraverses();
            }
            if (false && removes.size() > 0) {
                log.info("active 0 " + executorService.getActiveCount());
                executorService.purge();
                log.info("active 1 " + executorService.getActiveCount());
            }
            if (new Queues(nodeConf, controlService).getTraverseQueueSize() == 0 || new Queues(nodeConf, controlService).convertQueueHeavyLoaded()) {
                if (new Queues(nodeConf, controlService).convertQueueHeavyLoaded()) {
                    log.info("Convert queue heavy loaded, sleeping");
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

            nThreads = 1;
            nThreads = 3;
            for(int i = running; i < nThreads; i++) {
                Callable<Object> callable = new Callable<Object>() {
                    public Object call() /* throws Exception*/ {
                        try {
                            Queue<MyLock> locks = new ConcurrentLinkedQueue<>();
                            Queue<MySemaphore> semaphores = new ConcurrentLinkedQueue<>();
                            while (true) {
                                doTraverseTimeout(locks, semaphores);
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
                        return null; //myMethod();
                    }       
                };      

                Future<Object> task = executorService.submit(callable);
                map.put(task, new Date());
                new Queues(nodeConf, controlService).queueStat();
                new Queues(nodeConf, controlService).incTraverses();
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

    private void doTraverseTimeout(Queue<MyLock> locks, Queue<MySemaphore> semaphores) {
        int limit = nodeConf.getMPBatch();
        if (limit < 1) {
            limit = LIMIT;
        }
        MyQueue<TraverseQueueElement> queue = new Queues(nodeConf, controlService).getTraverseQueue();
        List<TraverseQueueElement> traverseList = new ArrayList<>();
        // limit = 1;
        for (int i = 0; i < limit; i++) {
            TraverseQueueElement trav = queue.poll(TraverseQueueElement.class);
            if (trav == null) {
                break;
            }
            if (!traverseList.isEmpty() && !traverseList.get(0).getFileobject().location.equals(trav.getFileobject().location)) {
                queue.offer(trav);
                break;
            }
            //Queues.getTraverseQueueSize().decrementAndGet();
            traverseList.add(trav);
            FileObject filename = trav.getFileobject();
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
            handleList3(traverseList, locks, semaphores);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            for (TraverseQueueElement trav : traverseList) {
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
            unlock(locks);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e); 
        }                   
        try {
            unlockSemaphores(semaphores);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e); 
        }                   
        try {
            TimeUnit.SECONDS.sleep(1);
            return;
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
    
    private void handleList3(List<TraverseQueueElement> traverseList, Queue<MyLock> locks, Queue<MySemaphore> semaphores) throws Exception {
        Set<FileObject> filenames = new HashSet<>();
        // Create filenames set
        for (TraverseQueueElement trav : traverseList) {
            filenames.add(trav.getFileobject());
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
        for (TraverseQueueElement trav : traverseList) {
            traverseFile.handleFo(trav, filenameMd5Map, ifMap, filenameNewMd5Map, locks, semaphores);
        }
        long time4 = System.currentTimeMillis();
        log.info("Times {} {} {} {}", usedTime(time1, time0), usedTime(time2, time1), usedTime(time3, time2), usedTime(time4, time3));
    }

    private int usedTime(long time2, long time1) {
        return (int) (time2 - time1); // 1000;
    }

}


