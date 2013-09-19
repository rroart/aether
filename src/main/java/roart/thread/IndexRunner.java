package roart.thread;

import java.io.ByteArrayOutputStream;
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
import roart.search.SearchLucene;

public class IndexRunner implements Runnable {
	
	private static Log log = LogFactory.getLog("IndexRunner");
	
    public void run() {
    	while (true) {
    		if (Queues.indexQueue.isEmpty()) {
    			try {
    				TimeUnit.SECONDS.sleep(1);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				log.error("Exception", e);
    			}
    			continue;
    		}
    		Queues.queueStat();
    		SearchLucene.indexme();
    	}
    }

}