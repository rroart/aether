package roart.thread;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;
import roart.common.filesystem.MyFile;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.dir.TraverseFile;
import roart.queue.TraverseQueueElement;
import roart.util.MyQueue;
import roart.util.MyQueues;

public class TraverseQueueRunner implements Runnable {

    static Logger log = LoggerFactory.getLogger(TraverseQueueRunner.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static final int LIMIT = 10;

    @SuppressWarnings("squid:S2189")
    public void run() {

        int cpu = 1;
        int nThreads = (int) (Runtime.getRuntime().availableProcessors() * cpu);
        log.info("nthreads {}", nThreads);
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

        String queueid = Constants.TRAVERSEQUEUE;
        MyQueue<TraverseQueueElement> queue = MyQueues.get(queueid);

        while (true) {
            List<TraverseQueueElement> traverseList = new ArrayList<>();
            for (int i = 0; i < LIMIT; i++) {
                TraverseQueueElement trav = queue.poll();
                if (trav == null) {
                    break;
                }
                if (!traverseList.isEmpty() && !traverseList.get(0).getFileobject().location.equals(trav.getFileobject().location)) {
                    break;
                }
                traverseList.add(trav);
                FileObject filename = trav.getFileobject();
                log.info("trav cli {}", filename);
            }
            if (traverseList.isEmpty()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                } catch (InterruptedException e) {
                    log.error(Constants.EXCEPTION, e); 
                }    		    
            }
            log.info("Traverse list size {}", traverseList.size());

            try {
                //handleList(pool, traverseList);
                handleList2(traverseList);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            } catch (Error e) {
                System.gc();
                log.error("Error " + Thread.currentThread().getId());
                log.error(Constants.ERROR, e);
            }
            finally {
                log.info("myend");            
            }
            try {
                TimeUnit.SECONDS.sleep(1);
                continue;
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e); 
            }                   
        }
    }

    private void handleList2(List<TraverseQueueElement> traverseList) throws Exception {
        Set<FileObject> filenames = new HashSet<>();
        for (TraverseQueueElement trav : traverseList) {
            TraverseFile.handleFo3(trav, filenames);
        }
        long time0 = System.currentTimeMillis();
        Map<FileObject, MyFile> fsMap = TraverseFile.handleFo3(filenames);
        long time1 = System.currentTimeMillis();
        Map<FileObject, String> md5Map = TraverseFile.handleFo4(filenames);
        long time2 = System.currentTimeMillis();
        Map<String, IndexFiles> ifMap = TraverseFile.handleFo5(new HashSet<>(md5Map.values().stream().filter(e -> e != null).collect(Collectors.toList())));
        long time3 = System.currentTimeMillis();
        for (TraverseQueueElement trav : traverseList) {
            TraverseFile.handleFo3(trav, fsMap, md5Map, ifMap);
        }
        long time4 = System.currentTimeMillis();
        log.info("Times {} {} {} {}", usedTime(time1, time0), usedTime(time2, time1), usedTime(time3, time2), usedTime(time4, time3));
    }

    private int usedTime(long time2, long time1) {
        return (int) (time2 - time1); // 1000;
    }
    
    private void handleList(ThreadPoolExecutor pool, List<TraverseQueueElement> traverseList) throws Exception {
        for (TraverseQueueElement trav : traverseList) {
            //TraverseFile.handleFo3(trav);
            Runnable runnable = new MyRunnable(trav);
            pool.execute(runnable);
        }        
    }

    class MyRunnable implements Runnable {
        TraverseQueueElement trav;

        public MyRunnable(TraverseQueueElement trav) {
            super();
            this.trav = trav;
        }

        @Override
        public void run() {
            try {
                TraverseFile.handleFo3(trav);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }

    }
}


