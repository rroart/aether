package roart.thread;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.dir.Traverse;
import roart.queue.Queues;
import roart.search.Search;
import roart.util.Constants;

public class IndexRunner implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger("IndexRunner");
	
    public void run() {
    	while (true) {
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
    	}
    }

}
