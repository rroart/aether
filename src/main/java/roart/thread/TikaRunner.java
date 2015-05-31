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

import roart.content.TikaHandler;
import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.util.Constants;

public class TikaRunner implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(TikaRunner.class);
	
    public static volatile int timeout = 3600;

    final int NTHREDS = 2;

    public void run() {
    	Map<Future<Object>, Date> map = new HashMap<Future<Object>, Date>();
    	int nThreads = Runtime.getRuntime().availableProcessors() / 4;
	if (nThreads == 0) {
	    nThreads = 1;
	}
    	int running = 0;
    	log.info("nthreads " + nThreads);
    	ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

    	if (Queues.getTikas() > 0) {
    		log.info("resetting tikas");
    		Queues.resetTikas();
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
    			
    			//Queues.decTikas();
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
    	            Queues.decTikas();
    	        } catch (ExecutionException e) {
    	            throw new RuntimeException(e);
    	        } catch (TimeoutException e) {
    	            log.error("timeout...", e);
    	            log.error("timeout and removing " + task);
    	            removes.add(task);
    	            Queues.decTikas();
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
    			Queues.decTikas();
    		}
    		if (false && removes.size() > 0) {
    		log.info("active 0 " + executorService.getActiveCount());
    		executorService.purge();
    		log.info("active 1 " + executorService.getActiveCount());
    		}
   		if (Queues.tikaQueue.isEmpty() || Queues.otherQueueHeavyLoaded() || Queues.indexQueueHeavyLoaded()) {
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
    						doTikaTimeout();
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
    			Queues.incTikas();
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

    public static String doTikaTimeout() {
   class TikaTimeout implements Runnable {
	   private TikaQueueElement el;
	   TikaTimeout(TikaQueueElement el) {
		   this.el = el;
	   }
	   
    	public void run() {
    		try {
    			new TikaHandler().doTika(el);
    		} catch (Exception e) {
    			log.error(Constants.EXCEPTION, e);
    		}
    	}
    }
    
	TikaQueueElement el = Queues.tikaQueue.poll();
	if (el == null) {
		log.error("empty queue");
	    return null;
	}
         TikaTimeout tikaRunnable = new TikaTimeout(el);
    	Thread tikaWorker = new Thread(tikaRunnable);
    	tikaWorker.setName("TikaTimeout");
    	tikaWorker.start();
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
    		if (!tikaWorker.isAlive()) {
    			log.info("Tikaworker finished " + tikaWorker + " " + tikaRunnable);
    			return null;
    		}
    	}
    	tikaWorker.stop(); // .interrupt();
	el.index.setTimeoutreason(el.index.getTimeoutreason() + "tikatimeout" + timeout + " ");
		log.info("Tikaworker timeout " + el.dbfilename + " " + tikaWorker + " " + tikaRunnable);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.error(Constants.EXCEPTION, e);
			// TODO Auto-generated catch block
		}
		log.info("Tikaworker timeout " + tikaWorker + " " + tikaRunnable + " " + tikaWorker.isAlive() + " " + tikaWorker.isInterrupted() + " " + tikaWorker.interrupted());
		Queues.otherQueue.add(el);
		return (String) null;
    }
    
    public static String doTikaTimeout2() {
        Callable<Object> callable = new Callable<Object>() {
            public Object call() throws Exception {
            	/*
            	TikaQueueElement el = Queues.tikaQueue.poll();
            	if (el == null) {
            		log.error("empty queue");
            	    return null;
            	}
            	*/
             new TikaHandler().doTika(null); // CHECK fix if changes
                return null;
            }
        };
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<Object> task = executorService.submit(callable);
        Object result = null;
        //TikaQueueElement result = null;
        
        try {
            // ok, wait for n seconds max
            result = (TikaQueueElement) task.get(timeout, TimeUnit.SECONDS);
            log.info("Finished with result: " + result);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            //log.error("timeout...", e);
            log.error("timeout and removing " + task);
            //Queues.decTikas();
            //TikaQueueElement el = result;
            //Queues.tikaRunQueue.remove(el);
        } catch (InterruptedException e) {
            log.error("interrupted", e);
        }
        List list = executorService.shutdownNow();
        log.error("Shutdown now list size " + list.size());
        return (String) result;
    }

}
