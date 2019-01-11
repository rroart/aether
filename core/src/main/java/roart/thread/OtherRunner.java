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

import roart.common.constants.Constants;
import roart.content.OtherHandler;
import roart.dir.Traverse;
import roart.queue.Queues;

public class OtherRunner implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(OtherRunner.class);
	
    public void run() {
    	if (Queues.getOthers() > 0) {
    		log.info("resetting others");
    		Queues.resetOthers();
    	}
    	
    	while (true) {
	    if (Queues.otherQueue.isEmpty() || Queues.indexQueueHeavyLoaded()) {
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
		    OtherHandler.doOther();    	
		} catch (Exception e) {
		    log.error(Constants.EXCEPTION, e);
		}
     	}
    }

}
