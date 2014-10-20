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

import roart.service.ControlService;

public class ControlRunner implements Runnable {
	
	private static Log log = LogFactory.getLog("ControlRunner");
	
    public void run() {
    	while (true) {
    			try {	
    				TimeUnit.SECONDS.sleep(60);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				log.error("Exception", e);
    			}
    			if (ControlService.tikaWorker.isAlive()) {
    				
    			}
     	}
    }

}
