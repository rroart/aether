package roart.thread;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
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

import roart.queue.Queues;
import roart.util.Constants;
import roart.database.IndexFilesDao;

import com.vaadin.ui.UI;

public class DbRunner implements Runnable {
	
    private static Logger log = LoggerFactory.getLogger(DbRunner.class);

    public static final Set<UI> uiset = new HashSet<UI>();
	
    final int update = 300;
    static long lastupdate = 0;

    public static volatile boolean doupdate = true;

    public void run() {
    	Set<Future<Object>> set = new HashSet<Future<Object>>();
	int nThreads = 4;
    	ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
    	while (true) {
	    long now = System.currentTimeMillis();
	    if ((now - lastupdate) >= update * 1000) {
		try {
		    if (doupdate) {
			IndexFilesDao.commit();
		    }
		} catch (Exception e) {
		    log.error(Constants.EXCEPTION, e);
		}
		lastupdate = now;
	    }
	    if (true) {
		try {
		    TimeUnit.SECONDS.sleep(update);
		} catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    log.error(Constants.EXCEPTION, e);
		    ClientRunner.notify("Db exception");
		}
	    }

     	}
    }

}
