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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import roart.dir.Traverse;
import roart.queue.Queues;

public class TikaRunner implements Runnable {
	
	private static Log log = LogFactory.getLog("QueueRunner");
	
    int NTHREDS = 2;

    public void run() {
    	Map<Future<Object>, Date> map = new HashMap<Future<Object>, Date>();
    	int nThreads = Runtime.getRuntime().availableProcessors() / 4;
    	log.info("nthreads " + nThreads);
    	ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    	while (true) {
    		long now = new Date().getTime();
    		List<Future> removes = new ArrayList<Future>();
    		for(Future<Object> task: map.keySet()) {
    			if (task.isDone()) {
    				log.info("removing " + task);
    				removes.add(task);    				
        			Date d = map.get(task);
        			if ( d != null) {
        				log.info("timerStop " + (d.getTime() - now));
        			}
    			}
    			if (task.isCancelled()) {
    				log.info("cancelled and removing " + task);
    				removes.add(task);    				
    			}
    			Date d = map.get(task);
    			if ( d != null && (now - d.getTime()) < 10 * 60 * 1000) {
    				continue;
    			}
    	        try {
    	            // ok, wait for 600 seconds max
    	            Object result = task.get(1, TimeUnit.MILLISECONDS);
    	            log.info("does this get here finished and removing " + task);
    	            removes.add(task);
    	            Queues.decTikas();
    	        } catch (ExecutionException e) {
    	            throw new RuntimeException(e);
    	        } catch (TimeoutException e) {
    	            log.error("timeout...", e);
    	            log.error("timeout and removing " + task);
    	            removes.add(task);
    	            Queues.decTikas();
    	        } catch (InterruptedException e) {
    	            log.error("interrupted", e);
    	        }	
    		}
    		for (Future<Object> key: removes) {
    			map.remove(key);
    		}
    		if (Queues.tikaQueue.isEmpty()) {
    			try {
    				TimeUnit.SECONDS.sleep(1);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				log.error("Exception", e);
    			}
    			continue;
    		}
    		Queues.queueStat();
    	
    		for(int i = 0; i < Queues.tikaQueue.size(); i++) {
    			Callable<Object> callable = new Callable<Object>() {
    				public Object call() throws Exception {
    					Traverse.doTika();
    					return null; //myMethod();
    				}	
    			};	
 
    			Future<Object> task = executorService.submit(callable);
    			map.put(task, new Date());
    		}
			try {
				TimeUnit.SECONDS.sleep(60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				log.error("Exception", e);
			}
    	}
    }

}