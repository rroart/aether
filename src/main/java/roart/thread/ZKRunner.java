package roart.thread;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.util.Constants;
import roart.zkutil.ZKWriteLock;
import roart.database.IndexFilesDao;
import roart.service.ControlService;

public class ZKRunner implements Runnable {
	
    static Logger log = LoggerFactory.getLogger(ZKRunner.class);

    final int update = 300;
    static long lastupdate = 0;

    public static volatile MyWatcher watcher = null;

    public static volatile ZKWriteLock writelock = null;

    public void run() {

	List<String> children = null;

    final String lockdir = "/" + Constants.AETHER + "/" + Constants.LOCK;
    
	watcher = new MyWatcher();
	initZK(watcher);
	writelock = new ZKWriteLock(zk, lockdir);
	String dir = "/" + Constants.AETHER + "/" + Constants.NODES + "/" + ControlService.nodename;

    	while (true) {
	    long now = System.currentTimeMillis();
	    if ((now - lastupdate) >= update * 1000) {
		try {
		    children = getChildren(dir, watcher);
		} catch (Exception e) {
		    log.error(Constants.EXCEPTION, e);
		}
		try {
		    if (children.size() == 0) {
			watcher.await();
			continue;
		    } else {
			readMsg(dir, children);
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
		    log.error(Constants.EXCEPTION, e);
		    ClientRunner.notify("ZK exception");
		}
	    }

     	}
    }

    private List<String> getChildren(String dir, Watcher watcher) throws KeeperException, InterruptedException {
        try {
            return zk.getChildren(dir, watcher);
        } catch (KeeperException.NoNodeException e){
            throw e;
        }
    }

    static ZooKeeper zk = null;

    private static void initZK(Watcher watcher) {
	    if (zk != null) {
		return;
	    }
	    try {
		zk = new ZooKeeper(ControlService.zookeeper, Integer.MAX_VALUE, watcher);
		Stat s;
		s = zk.exists("/" + Constants.AETHER, false);
		if (s == null) {
		    zk.create("/" + Constants.AETHER, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		s = zk.exists("/" + Constants.AETHER + "/" + Constants.LOCK, false);
		if (s == null) {
		    zk.create("/" + Constants.AETHER + "/" + Constants.LOCK, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		s = zk.exists("/" + Constants.AETHER + "/" + Constants.NODES, false);
		if (s == null) {
		    zk.create("/" + Constants.AETHER + "/" + Constants.NODES, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		s = zk.exists("/" + Constants.AETHER + "/" + Constants.NODES + "/" + ControlService.nodename, false);
		if (s == null) {
		    zk.create("/" + Constants.AETHER + "/" + Constants.NODES + "/" + ControlService.nodename, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
    }

    public static class MyWatcher implements Watcher {

	CountDownLatch latch;

	Integer mutex = null;

	public MyWatcher() {
	    try {
		latch = new CountDownLatch(1);
		mutex = new Integer(-1);
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	}

	public void process(WatchedEvent event) {
	    log.info("Process " + event.getPath() + " state: " + event.getState() + " type " + event.getType());
	    if (event.getPath() == null) {
		return;
	    }
	    if (event.getPath().contains("/" + Constants.AETHER + "/" + Constants.LOCK)) {
		try {
		    writelock.lock();
		} catch (Exception e) {
		    log.info(Constants.EXCEPTION, e);
		}
	    } else {
		latch.countDown();
	    }
	}

        public void await() throws InterruptedException {
            latch.await();
        }

    }

    public void readMsg(String dir, List<String> children) {
	try {
	    for (String child : children) {
		if (child.equals(Constants.REFRESH)) {
		    IndexFilesDao.getAll();
		    log.info(Constants.REFRESH + " " + ControlService.nodename);
		} else {
		    log.info("unknown command " + child);
		}
		zk.delete(dir + "/" + child, 0);
	    }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

    public static void dorefresh() {
	if (zk == null) {
	    return;
	}
	log.info("sendMsgRefresh");
	try {
	    List<String> nodes = zk.getChildren("/" + Constants.AETHER + "/" + Constants.NODES, false);
	    for (String node : nodes) {
		if (!ControlService.nodename.equals(node)) {
		    zk.create("/" + Constants.AETHER + "/" + Constants.NODES + "/" + node + "/" + Constants.REFRESH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		    log.info("send refresh to " + node);
		}
	    }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

    public static void unlockme() {
	log.info("unlockme");
	writelock.unlock();
    }

    public static void lockme() {
	log.info("lockme");
	boolean locked;
	try {
	    do {
		locked = writelock.lock();
		log.info("lockme " + locked);
		if (!locked) {
			TimeUnit.SECONDS.sleep(60);
		}
	    } while (!locked);
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

}
