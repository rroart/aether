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

import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.service.ControlService;

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
    			if (!ControlService.indexWorker.isAlive()) {
    				cs.startIndexWorker();
    			}
    			if (!ControlService.dbWorker.isAlive()) {
    				cs.startDbWorker();
    			}
    			if (MyConfig.conf.getZookeeper() != null && !ControlService.zkWorker.isAlive()) {
    				//cs.startZKWorker();
    			}
                if (!ControlService.traverseQueueWorker.isAlive()) {
                    cs.startTraversequeueWorker();
                }
                //if (ControlService.zookeeper != null && ControlService.zookeepersmall && !ControlService.zkQueueWorker.isAlive()) {
                //    cs.startZKQueueWorker();
                //}
     	}
    }

    public static int getThreads() {
        double cpu = MyConfig.instance().conf.getMPCpu();
        int nThreads = (int) (Runtime.getRuntime().availableProcessors() * cpu);
        if (nThreads < 1) {
            nThreads = 1;
        }
        return nThreads;
    }
}
