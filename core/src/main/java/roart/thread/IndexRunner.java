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

import roart.common.collections.MyCollection;
import roart.common.collections.MyQueue;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.dir.Traverse;
import roart.hcutil.GetHazelcastInstance;
import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.search.Search;
import roart.service.ControlService;
import roart.common.queue.QueueElement;
import roart.common.synchronization.MyObjectLock;
import roart.common.synchronization.MySemaphore;
import roart.common.synchronization.impl.MyObjectLockFactory;
import roart.common.synchronization.impl.MySemaphoreFactory;

public class IndexRunner implements Runnable {

    private static Logger log = LoggerFactory.getLogger(IndexRunner.class);

    public static volatile int timeout = 3600;

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public IndexRunner(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public void run() {
        Map<Future<Object>, Date> map = new HashMap<Future<Object>, Date>();
        int nThreads = ControlRunner.getThreads();
        nThreads = nodeConf.getMPThreadsIndex();
        int running = 0;
        log.info("nthreads {}", nThreads);
        ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

        if (new Queues(nodeConf, controlService).getIndexs() > 0) {
            log.info("resetting indexs");
            //Queues.resetIndexs();
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
                new Queues(nodeConf, controlService).decIndexs();
            }
            if (new Queues(nodeConf, controlService).getIndexQueueSize() == 0 /*|| Queues.indexQueueHeavyLoaded()*/) {
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
                new Queues(nodeConf, controlService).queueStat();
                new Queues(nodeConf, controlService).incIndexs();
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

    public String doIndexTimeout() {
        class IndexTimeout implements Runnable {
            private QueueElement el;
            IndexTimeout(QueueElement el) {
                this.el = el;
            }

            public void run() {
                try {
                    if (nodeConf.wantAsync()) {
                    new Search(nodeConf, controlService).indexmeQueue(el);
                    } else {
                    new Search(nodeConf, controlService).indexme(el);
                    }
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            }
        }

        MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getIndexQueue();
        QueueElement el = queue.poll(QueueElement.class);
        if (el == null) {
            log.error("empty queue");
            return null;
        }
        try {
            if (!nodeConf.wantAsync()) {
                MySemaphore lock = MySemaphoreFactory.create(el.getMd5(), nodeConf.getLocker(), controlService.curatorClient, GetHazelcastInstance.instance(nodeConf));
                boolean locked = lock.tryLock();
                if (!locked) {
                    queue.offer(el);
                    return null;
                }
                el.getIndexFiles().setSemaphorelock(lock);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            queue.offer(el);
        }
        //Queues.getIndexQueueSize().decrementAndGet();
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
        el.getIndexFiles().setTimeoutreason("indextimeout" + timeout);
        log.info("Indexworker timeout " + el.getMd5() + " " + indexWorker + " " + indexRunnable);
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
