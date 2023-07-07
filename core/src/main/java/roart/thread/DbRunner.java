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
import roart.common.constants.Constants;
import roart.database.IndexFilesDao;

public class DbRunner implements Runnable {
	
    private static final Logger log = LoggerFactory.getLogger(DbRunner.class);

    static final int update = 2;
    static long lastupdate = 0;

    //public static volatile boolean doupdate = true;

    public void run() {
        IndexFilesDao indexFilesDao = new IndexFilesDao();
        while (true) {
            long now = System.currentTimeMillis();
            log.info("updatetime " + (int) ((now - lastupdate)/1000));
            if (true || (now - lastupdate) >= update * 1000) {
                try {
                    if (true /*doupdate*/) {
                        indexFilesDao.commit();
                    }
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
                lastupdate = System.currentTimeMillis();
            }
            try {
                int sleepsec = update - (int) ((lastupdate - now)/1000);  
                if (sleepsec < 1) {
                    sleepsec = 1;
                }
                log.info("sleepsec " + sleepsec);
                TimeUnit.SECONDS.sleep(sleepsec);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                log.error(Constants.EXCEPTION, e);
                //ClientRunner.notify("Db exception");
            }
        }
    }

}
