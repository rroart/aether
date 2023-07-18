package roart.thread;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.dir.Traverse;
import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.search.Search;

public class IndexRunner implements Runnable {

    private static Logger log = LoggerFactory.getLogger(IndexRunner.class);

    public static volatile int timeout = 3600;

    public void run() {
        Map<Future<Object>, Date> map = new HashMap<Future<Object>, Date>();
        int nThreads = ControlRunner.getThreads();
        nThreads = MyConfig.instance().conf.getMPThreadsIndex();
        int running = 0;
        log.info("nthreads {}", nThreads);
        ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

        if (Queues.getIndexs() > 0) {
            log.info("resetting indexs");
            Queues.resetIndexs();
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

                //Queues.decIndexs();
                log.error("timeout and removing " + task + " " + map.size());
                boolean ok = task.cancel(true);
                if (!ok) {
                    log.error("canceled error");
                }
            }
            for (Future<Object> key: removes) {
                map.remove(key);
                running--;
                Queues.decIndexs();
            }
            if (Queues.getIndexQueue().size() == 0 /*|| Queues.indexQueueHeavyLoaded()*/) {
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
                            doIndexTimeout();
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
                Queues.incIndexs();
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
            /*
    		if (Queues.indexQueue.isEmpty()) {
    			try {
    				TimeUnit.SECONDS.sleep(1);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				log.error(Constants.EXCEPTION, e);
    			}
    			continue;
    		}
    		Queues.queueStat();
		try {
		    Search.indexme();
		} catch (Exception e) {
		    log.error(Constants.EXCEPTION, e);
		}
             */
        }
    }

    public static String doIndexTimeout() {
        class IndexTimeout implements Runnable {
            private IndexQueueElement el;
            IndexTimeout(IndexQueueElement el) {
                this.el = el;
            }

            public void run() {
                try {
                    Search.indexme(el);
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            }
        }

        IndexQueueElement el = Queues.getIndexQueue().poll(IndexQueueElement.class);
        if (el == null) {
            log.error("empty queue");
            return null;
        }
        IndexTimeout indexRunnable = new IndexTimeout(el);
        Thread indexWorker = new Thread(indexRunnable);
        indexWorker.setName("IndexTimeout");
        indexWorker.start();
        long start = System.currentTimeMillis();
        boolean b = true;
        while (b) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e);
                // TODO Auto-generated catch block
            }
            long now = System.currentTimeMillis();
            if ((now - start) > 1000 * timeout) {
                b = false;
            }
            if (!indexWorker.isAlive()) {
                log.info("Indexworker finished " + indexWorker + " " + indexRunnable);
                return null;
            }
        }
        indexWorker.stop(); // .interrupt();
        el.index.setTimeoutreason(el.index.getTimeoutreason() + "indextimeout" + timeout + " ");
        log.info("Indexworker timeout " + el.md5 + " " + indexWorker + " " + indexRunnable);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e);
            // TODO Auto-generated catch block
        }
        log.info("Indexworker timeout " + indexWorker + " " + indexRunnable + " " + indexWorker.isAlive() + " " + indexWorker.isInterrupted() + " " + indexWorker.interrupted());
        // TODO not needed here anymore? double insertion to otherQueue bug 
        //Queues.otherQueue.add(el);
        return (String) null;
    }
}
