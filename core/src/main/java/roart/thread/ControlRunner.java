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

import roart.config.MyConfig;
import roart.service.ControlService;
import roart.util.Constants;

public class ControlRunner implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(ControlRunner.class);
	
    public void run() {
    	while (true) {
    			try {	
    				TimeUnit.SECONDS.sleep(60);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				log.error(Constants.EXCEPTION, e);
    			}
    			ControlService cs = new ControlService();
    			if (!ControlService.tikaWorker.isAlive()) {
    				cs.startTikaWorker();
    			}
    			if (!ControlService.indexWorker.isAlive()) {
    				cs.startIndexWorker();
    			}
    			if (!ControlService.otherWorker.isAlive()) {
    				cs.startOtherWorker();
    			}
    			if (!ControlService.dbWorker.isAlive()) {
    				cs.startDbWorker();
    			}
    			if (MyConfig.conf.zookeeper != null && !ControlService.zkWorker.isAlive()) {
    				cs.startZKWorker();
    			}
                if (!ControlService.traverseQueueWorker.isAlive()) {
                    cs.startTraversequeueWorker();
                }
                //if (ControlService.zookeeper != null && ControlService.zookeepersmall && !ControlService.zkQueueWorker.isAlive()) {
                //    cs.startZKQueueWorker();
                //}
     	}
    }

}
