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
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.content.ConvertHandler;
import roart.content.ConvertHandler;
import roart.queue.ConvertQueueElement;
import roart.queue.Queues;
import roart.queue.ConvertQueueElement;

public class ConvertRunner implements Runnable {

    private static Logger log = LoggerFactory.getLogger(ConvertRunner.class);

    public static volatile int timeout = 3600;

    final int NTHREDS = 2;

    private NodeConfig nodeConf;

    public ConvertRunner(NodeConfig nodeConf) {
        super();
        this.nodeConf = nodeConf;
    }

    public NodeConfig getNodeConf() {
        return nodeConf;
    }

    public void setNodeConf(NodeConfig nodeConf) {
        this.nodeConf = nodeConf;
    }

    public void run() {
        Map<Future<Object>, Date> map = new HashMap<Future<Object>, Date>();
        int nThreads = ControlRunner.getThreads();
        nThreads = nodeConf.getMPThreadsConvert();
        int running = 0;
        log.info("nthreads " + nThreads);
        ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

        if (new Queues(nodeConf).getConverts() > 0) {
            log.info("resetting converts");
            //Queues.resetConverts();
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

                //Queues.decConverts();
                log.error("timeout and removing " + task + " " + map.size());
                boolean ok = task.cancel(true);
                if (!ok) {
                    log.error("canceled error");
                }
                /*              
                try {
                        Object result = task.get(1, TimeUnit.MILLISECONDS);
                        log.info("does this get here finished and removing " + task + " " + map.size());
                    removes.add(task);
                    Queues.decConverts();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    log.error("timeout...", e);
                    log.error("timeout and removing " + task);
                    removes.add(task);
                    Queues.decConverts();
                        boolean ok = task.cancel(true);
                        if (!ok) {
                                log.error("canceled error");
                        }
                    //Thread.currentThread().interrupt();                   
                    //task.cancel(true);
                    //FutureTask<Object> ft = (FutureTask<Object>) task;
                    //ft.run();
                } catch (InterruptedException e) {
                    log.error("interrupted", e);
                }       
                 */
            }
            for (Future<Object> key: removes) {
                map.remove(key);
                running--;
                new Queues(nodeConf).decConverts();
            }
            if (false && removes.size() > 0) {
                log.info("active 0 " + executorService.getActiveCount());
                executorService.purge();
                log.info("active 1 " + executorService.getActiveCount());
            }
            if (new Queues(nodeConf).getConvertQueueSize() == 0 || new Queues(nodeConf).indexQueueHeavyLoaded()) {
                if (new Queues(nodeConf).indexQueueHeavyLoaded()) {
                    log.info("Index queue heavy loaded, sleeping");
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
                            doConvertTimeout(nodeConf);
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
                new Queues(nodeConf).queueStat();
                new Queues(nodeConf).incConverts();
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

    public static String doConvertTimeout(NodeConfig nodeConf) {
        class ConvertTimeout implements Runnable {
            private ConvertQueueElement el;
            ConvertTimeout(ConvertQueueElement el) {
                this.el = el;
            }

            public void run() {
                try {
                    new ConvertHandler(nodeConf).doConvert(el, nodeConf);
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            }
        }

        ConvertQueueElement el = new Queues(nodeConf).getConvertQueue().poll(ConvertQueueElement.class);
        if (el == null) {
            log.error("empty queue");
            return null;
        }
        //Queues.getConvertQueueSize().decrementAndGet();
        ConvertTimeout convertRunnable = new ConvertTimeout(el);
        Thread convertWorker = new Thread(convertRunnable);
        convertWorker.setName("ConvertTimeout");
        convertWorker.start();
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
            if (!convertWorker.isAlive()) {
                log.info("Convertworker finished " + convertWorker + " " + convertRunnable);
                return null;
            }
        }
        convertWorker.stop(); // .interrupt();
        el.index.setTimeoutreason("converttimeout" + timeout);
        log.info("Convertworker timeout " + el.filename + " " + convertWorker + " " + convertRunnable);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e);
            // TODO Auto-generated catch block
        }
        log.info("Convertworker timeout " + convertWorker + " " + convertRunnable + " " + convertWorker.isAlive() + " " + convertWorker.isInterrupted() + " " + convertWorker.interrupted());
        // TODO not needed here anymore? double insertion to otherQueue bug 
        //Queues.otherQueue.add(el);
        return (String) null;
    }

    // not used

    public static String doConvertTimeout2(NodeConfig nodeConf) {
        Callable<Object> callable = new Callable<Object>() {
            public Object call() throws Exception {
                /*
                ConvertQueueElement el = Queues.convertQueue.poll();
                if (el == null) {
                        log.error("empty queue");
                    return null;
                }
                 */
                new ConvertHandler(nodeConf).doConvert(null, nodeConf); // CHECK fix if changes
                return null;
            }
        };
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<Object> task = executorService.submit(callable);
        Object result = null;
        //ConvertQueueElement result = null;

        try {
            // ok, wait for n seconds max
            result = (ConvertQueueElement) task.get(timeout, TimeUnit.SECONDS);
            log.info("Finished with result: " + result);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            //log.error("timeout...", e);
            log.error("timeout and removing " + task);
            //Queues.decConverts();
            //ConvertQueueElement el = result;
            //Queues.convertRunQueue.remove(el);
        } catch (InterruptedException e) {
            log.error("interrupted", e);
        }
        List list = executorService.shutdownNow();
        log.error("Shutdown now list size " + list.size());
        return (String) null;
    }

}
