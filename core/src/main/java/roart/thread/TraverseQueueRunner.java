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

import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.filesystem.MyFile;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.database.IndexFilesDao;
import roart.dir.TraverseFile;
import roart.filesystem.FileSystemDao;
import roart.model.MyQueue;
import roart.model.MyQueues;
import roart.queue.Queues;
import roart.queue.TraverseQueueElement;

public class TraverseQueueRunner implements Runnable {

    static Logger log = LoggerFactory.getLogger(TraverseQueueRunner.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static final int LIMIT = 100;

    private IndexFilesDao indexFilesDao = new IndexFilesDao();

    private TraverseFile traverseFile = new TraverseFile(indexFilesDao); 

    ThreadPoolExecutor /*ExecutorService*/ pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    @SuppressWarnings("squid:S2189")
    public void run() {
        Map<Future<Object>, Date> map = new HashMap<Future<Object>, Date>();
        int nThreads = ControlRunner.getThreads();
        nThreads = MyConfig.instance().conf.getMPThreadsFS();
        int running = 0;
        log.info("nthreads {}", nThreads);
        ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

        if (Queues.getTraverses() > 0) {
            log.info("resetting traverses");
            Queues.resetTraverses();
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
                Queues.decTraverses();
            }
            if (false && removes.size() > 0) {
                log.info("active 0 " + executorService.getActiveCount());
                executorService.purge();
                log.info("active 1 " + executorService.getActiveCount());
            }
            if (Queues.getTraverseQueue().size() == 0 || Queues.convertQueueHeavyLoaded()) {
                if (Queues.convertQueueHeavyLoaded()) {
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

            for(int i = running; i < nThreads; i++) {
                Callable<Object> callable = new Callable<Object>() {
                    public Object call() /* throws Exception*/ {
                        try {
                            doTraverseTimeout();
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
                Queues.queueStat();
                Queues.incTraverses();
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

    private void doTraverseTimeout() {
        int limit = MyConfig.instance().conf.getMPBatch();
        if (limit < 1) {
            limit = LIMIT;
        }
        MyQueue<TraverseQueueElement> queue = Queues.getTraverseQueue();
        List<TraverseQueueElement> traverseList = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            TraverseQueueElement trav = queue.poll();
            if (trav == null) {
                break;
            }
            if (!traverseList.isEmpty() && !traverseList.get(0).getFileobject().location.equals(trav.getFileobject().location)) {
                queue.offer(trav);
                break;
            }
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
            handleList3(pool, traverseList);
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

    /**
     * 
     * Traverse list of max limit
     * 
     * @param traverseList
     * @throws Exception
     */
    
    private void handleList2(List<TraverseQueueElement> traverseList) throws Exception {
        Set<FileObject> filenames = new HashSet<>();
        // Create filenames set
        for (TraverseQueueElement trav : traverseList) {
            filenames.add(trav.getFileobject());
        }
        long time0 = System.currentTimeMillis();
        // Get MyFile data from filesystem
        Map<FileObject, MyFile> fsMap = FileSystemDao.getWithoutInputStream(filenames);
        long time1 = System.currentTimeMillis();
        // Get Md5s by fileobject from database
        // TODO check if need full indexfiles?
        Map<FileObject, String> filenameMd5Map = indexFilesDao.getMd5ByFilename(filenames);
        // Batch read md5, if have none or wants to calculate new
        Map<FileObject, String> filenameNewMd5Map = traverseFile.getMd5(traverseList, fsMap, filenameMd5Map);
        // Batch read content
        Map<FileObject, String> contentMap = new HashMap<>(); // TraverseFile.readFiles(traverseList, fsMap);
        long time2 = System.currentTimeMillis();
        Map<String, IndexFiles> ifMap = indexFilesDao.getByMd5(new HashSet<>(filenameMd5Map.values().stream().filter(e -> e != null).collect(Collectors.toList())));
        // Get IndexFiles by Md5 from database
        long time3 = System.currentTimeMillis();
        // Do individual traverse, index etc
        for (TraverseQueueElement trav : traverseList) {
            traverseFile.handleFo(trav, fsMap, filenameMd5Map, ifMap, filenameNewMd5Map, contentMap);
        }
        long time4 = System.currentTimeMillis();
        log.info("Times {} {} {} {}", usedTime(time1, time0), usedTime(time2, time1), usedTime(time3, time2), usedTime(time4, time3));
    }

    private int usedTime(long time2, long time1) {
        return (int) (time2 - time1); // 1000;
    }

    @Deprecated // ?
    private void handleList(ThreadPoolExecutor pool, List<TraverseQueueElement> traverseList) throws Exception {
        for (TraverseQueueElement trav : traverseList) {
            //TraverseFile.handleFo3(trav);
            Runnable runnable = new MyRunnable(trav);
            pool.execute(runnable);
        }        
    }

    private void handleList3(ThreadPoolExecutor pool, List<TraverseQueueElement> traverseList) throws Exception {
        Runnable runnable = new MyRunnable3(traverseList);
        pool.execute(runnable);
    }
    
    @Deprecated // ?
    class MyRunnable implements Runnable {
        TraverseQueueElement trav;

        public MyRunnable(TraverseQueueElement trav) {
            super();
            this.trav = trav;
        }

        @Override
        public void run() {
            try {
                traverseFile.handleFo3(trav);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }

    }
    
    class MyRunnable3 implements Runnable {
        List<TraverseQueueElement> traverseList;

        public MyRunnable3(List<TraverseQueueElement> traverseList) {
            super();
            this.traverseList = traverseList;
        }

        @Override
        public void run() {
            try {
                handleList2(traverseList);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }

    }
}


