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

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.queue.Queues;
import roart.util.Constants;
import roart.database.IndexFilesDao;
import roart.service.ControlService;

import com.vaadin.ui.UI;

public class ZKRunner implements Runnable {
	
    private static Logger log = LoggerFactory.getLogger(ZKRunner.class);

    public static final Set<UI> uiset = new HashSet<UI>();
	
    final int update = 300;
    static long lastupdate = 0;

    public static volatile boolean doupdate = true;

    public static volatile boolean dorefresh = false;

    public static volatile MyWatcher watcher = null;

    public void run() {
    	Set<Future<Object>> set = new HashSet<Future<Object>>();
	int nThreads = 4;
    	ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

	watcher = new MyWatcher();

    	while (true) {
	    long now = System.currentTimeMillis();
	    if ((now - lastupdate) >= update * 1000) {
		try {
		    watcher.readMsgRefresh();
		    if (dorefresh) {
			dorefresh = false;
			watcher.sendMsgRefresh();
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
		    ClientRunner.notify("ZK exception");
		}
	    }

     	}
    }

    private static class MyWatcher implements Watcher {
	static ZooKeeper zk = null;

	Integer mutex = null;

	public MyWatcher() {
	    if (zk != null) {
		return;
	    }
	    try {
		zk = new ZooKeeper(ControlService.zookeeper, 3000, this);
		Stat s;
		s = zk.exists("/aether", false);
		if (s == null) {
		    zk.create("/aether", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		s = zk.exists("/aether/nodes", false);
		if (s == null) {
		    zk.create("/aether/nodes", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		s = zk.exists("/aether/nodes/" + ControlService.nodename, false);
		if (s == null) {
		    zk.create("/aether/nodes/" + ControlService.nodename, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		mutex = new Integer(-1);
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	}

	public void readMsgRefresh() {
	    try {
		synchronized (mutex) {
		    Stat s = zk.exists("/aether/nodes/" + ControlService.nodename + "/refresh", false);
		    if (s != null) {
			zk.delete("/aether/nodes/" + ControlService.nodename + "/refresh", 0);
			roart.database.IndexFilesDao.getAll();
			log.info("refresh " + ControlService.nodename);
		    }
		}
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	}

	public void sendMsgRefresh() {
	    log.info("sendMsgRefresh");
	    try {
		List<String> nodes = zk.getChildren("/aether/nodes", false);
		for (String node : nodes) {
		    if (!ControlService.nodename.equals(node)) {
			zk.create("/aether/nodes/" + node + "/refresh", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			log.info("send refresh to " + node);
		    }
		}
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	}

	synchronized public void process(WatchedEvent event) {
	    /*
	    synchronized (mutex) {
		//System.out.println("Process: " + event.getType());
		mutex.notify();
	    }
	    */
	}

    }

}
