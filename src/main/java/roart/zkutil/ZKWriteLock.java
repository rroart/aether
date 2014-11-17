package roart.zkutil;

import static org.apache.zookeeper.CreateMode.EPHEMERAL_SEQUENTIAL;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;

import roart.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKWriteLock {
	
	static Logger log = LoggerFactory.getLogger(ZKWriteLock.class);
	
    private ZooKeeper zookeeper;
    private String path = null;
    private String myName = null;
    private String ownerName;
    private String prevChildName;
    private ZKNode myNode;
    private ZKExecute zkexec;
    private ZKLockListener listener;
    
    public ZKWriteLock(ZooKeeper zookeeper, String path) {
        this.zookeeper = zookeeper;
        this.path = path;
        this.zkexec = new ZKLockExecute();
    }

    public ZKWriteLock(ZooKeeper zookeeper, String dir, ZKLockListener listener) {
    	this(zookeeper, dir);
    	this.listener = listener;
    }

public synchronized boolean lock() throws KeeperException, InterruptedException {
    ensureExists(path, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    return (Boolean) retryExecution(zkexec);
}

protected void ensureExists(final String path, final List<ACL> acl, final CreateMode flags) {
    try {
    	ZKExecute create = new ZKExecute() {
    	    public boolean execute() throws KeeperException, InterruptedException {
    	    	Stat stat = zookeeper.exists(path, false);
    	    	if (stat != null) {
    	    	    return true;
    	    	}
    	    	zookeeper.create(path, new byte[0], acl, flags);
    	    	return true;
    	    }
    	};
        retryExecution(create);
    } catch (Exception e) {
    	log.error(Constants.EXCEPTION, e);
    }
}

protected Object retryExecution(ZKExecute operation) throws KeeperException, InterruptedException {
    final int retries = 10;
    
    KeeperException exception = null;
    for (int i = 0; i < retries; i++) {
        try {
            return operation.execute();
        } catch (KeeperException.SessionExpiredException e) {
            log.info("Session expired " + zookeeper.getSessionId());
            log.error(Constants.EXCEPTION, e);
        	exception = e;
       } catch (KeeperException.ConnectionLossException e) {
            log.info("Retry failed " + i);
            log.error(Constants.EXCEPTION, e);
            increasedWait(i);
        	exception = e;
        }
    }
    throw exception;
}

public boolean isOwner() {
    return myName != null && ownerName != null && myName.equals(ownerName);
}

protected void increasedWait(int attempt) {
    final long timefactor = 500L;
    if (attempt > 0) {
        try {
            Thread.sleep(attempt * timefactor);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e);
        }
    }
}

public synchronized void unlock() throws RuntimeException {
    if (myName != null) {
        try {
            zookeeper.delete(myName, -1);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e);
            Thread.currentThread().interrupt();
        } catch (KeeperException.NoNodeException e) {
        	// nothing to do
        } catch (KeeperException e) {
            log.error(Constants.EXCEPTION, e);
            throw (RuntimeException) new RuntimeException(e.getMessage()).initCause(e);
        } finally {
            myName = null;
        }
    }
}
    
private class ZKLockExecute implements ZKExecute {

private String getChildrenWithPrefix(ZooKeeper zookeeper, String path, String prefix, String myname) throws KeeperException, InterruptedException {
    List<String> names = zookeeper.getChildren(path, false);
    for (String name : names) {
    	if (name.startsWith(prefix)) {
    		log.info("Previously created " + name);
    		return name;
    	}
    }
    if (myname == null) {
    	myname = zookeeper.create(path + "/" + prefix, new byte[0], Ids.OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL);
	    log.info("Created " + myname);
    }
    return myname;
}

       public boolean execute() throws KeeperException, InterruptedException {
           do {
               if (myName == null) {
                   long sessionId = zookeeper.getSessionId();
                   String prefix = "zk" + Constants.ZKDELIMITER + sessionId + Constants.ZKDELIMITER;
                   myName = getChildrenWithPrefix(zookeeper, path, prefix, myName);
                   myNode = new ZKNode(myName);
               }
               if (myName != null) {
                   List<String> children = zookeeper.getChildren(path, false);
                   if (children.isEmpty()) {
                       log.info("No children " + path);
                       myName = null;
                   } else {
                        SortedSet<ZKNode> sortedChildren = new TreeSet<ZKNode>();
                        for (String child : children) {
                            sortedChildren.add(new ZKNode(path + "/" + child));
                        }
                        ownerName = sortedChildren.first().getName();
                        SortedSet<ZKNode> beforeme = sortedChildren.headSet(myNode);
                        if (!beforeme.isEmpty()) {
                            ZKNode childbeforeme = beforeme.last();
                            prevChildName = childbeforeme.getName();
                            log.info("child before me " + prevChildName);
                            Stat stat = zookeeper.exists(prevChildName, new LockWatcher());
                            if (stat != null) {
                                return Boolean.FALSE;
                            }
                            log.info("Could not find the child before me " + childbeforeme.getName());
                        } else {
                            if (isOwner()) {
                                if (listener != null) {
                                    listener.acquire();
                                }
                                return Boolean.TRUE;
                            }
                        }
                   }
               }
           } while (myName == null);
           return Boolean.FALSE;
       }
    };

    private class LockWatcher implements Watcher {

	CountDownLatch latch;

	public LockWatcher() {
	    try {
		latch = new CountDownLatch(1);
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	}

	public void process(WatchedEvent event) {
	    log.info("Process " + event.getPath() + " state: " + event.getState() + " type " + event.getType());
	    if (event.getPath() == null) {
		return;
	    }
	    try {
		lock();
	    } catch (Exception e) {
		log.info(Constants.EXCEPTION, e);
	    }
	}

        public void await() throws InterruptedException {
            latch.await();
        }

    }

}
