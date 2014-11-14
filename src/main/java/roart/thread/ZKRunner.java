package roart.thread;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.CountDownLatch;

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
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import static org.apache.zookeeper.CreateMode.EPHEMERAL_SEQUENTIAL;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.atomic.AtomicBoolean;

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

    public static volatile MyWatcher watcher = null;

    static ZooKeeper zookeeper = null;
    private static AtomicBoolean closed = new AtomicBoolean(false);
    private static List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
    private static long retryDelay = 500L;
    private static int retryCount = 10;

    private static final String dir = "/aether/lock";
    private static String id = null;
    private static ZNodeName idName;
    private static String ownerId;
    private static String lastChildId;
    private static byte[] data = {0x12, 0x34};
    private static LockListener callback;
    private static LockZooKeeperOperation zop = new LockZooKeeperOperation();

    public void run() {
    	Set<Future<Object>> set = new HashSet<Future<Object>>();
	int nThreads = 4;
    	ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

	List<String> children = null;

	watcher = new MyWatcher();
	initZK(watcher);
	String dir = "/aether/nodes/" + ControlService.nodename;

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
		    // TODO Auto-generated catch block
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
		zookeeper = zk;
		Stat s;
		s = zk.exists("/aether", false);
		if (s == null) {
		    zk.create("/aether", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		s = zk.exists("/aether/lock", false);
		if (s == null) {
		    zk.create("/aether/lock", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		s = zk.exists("/aether/nodes", false);
		if (s == null) {
		    zk.create("/aether/nodes", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		s = zk.exists("/aether/nodes/" + ControlService.nodename, false);
		if (s == null) {
		    zk.create("/aether/nodes/" + ControlService.nodename, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
    }

    private static class MyWatcher implements Watcher {

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
	    log.info("Process " + event.getPath() + " state: " +
event.getState() + " type " + event.getType());
	    if (event.getPath() == null) {
		return;
	    }
	    if (event.getPath().contains("/aether/lock")) {
		try {
		    lock();
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
		if (child.equals("refresh")) {
		    roart.database.IndexFilesDao.getAll();
		    log.info("refresh " + ControlService.nodename);
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

    public synchronized static boolean lock() throws KeeperException, InterruptedException {
        if (isClosed()) {
            return false;
        }
        ensurePathExists(dir);

        return (Boolean) retryOperation(zop);
    }

    protected static boolean isClosed() {
        return closed.get();
    }

    protected static void ensurePathExists(String path) {
        ensureExists(path, null, acl, CreateMode.PERSISTENT);
    }

    protected static void ensureExists(final String path, final byte[] data,
				final List<ACL> acl, final CreateMode flags) {
        try {
            retryOperation(new ZooKeeperOperation() {
		    public boolean execute() throws KeeperException, InterruptedException {
			Stat stat = zookeeper.exists(path, false);
			if (stat != null) {
			    return true;
			}
			zookeeper.create(path, data, acl, flags);
			return true;
		    }
		});
        } catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
        }
    }

    protected static Object retryOperation(ZooKeeperOperation operation)
        throws KeeperException, InterruptedException {
        KeeperException exception = null;
        for (int i = 0; i < retryCount; i++) {
            try {
                return operation.execute();
            } catch (KeeperException.SessionExpiredException e) {
                log.info("Session expired for: " + zookeeper + " so reconnecting due to: " + e, e);
                throw e;
            } catch (KeeperException.ConnectionLossException e) {
                if (exception == null) {
                    exception = e;
                }
                log.info("Attempt " + i + " failed with connection loss so " +
			  "attempting to reconnect: " + e, e);
                retryDelay(i);
            }
        }
        throw exception;
    }

    private static interface ZooKeeperOperation { 
	public boolean execute() throws KeeperException, InterruptedException;
    }

    private static interface LockListener {
	public void lockAcquired();
	public void lockReleased();
    }

    public static synchronized void unlock() throws RuntimeException {
        if (!isClosed() && id != null) {
            try {
                ZooKeeperOperation zopdel = new ZooKeeperOperation() {
			public boolean execute() throws KeeperException, InterruptedException {
			    zookeeper.delete(id, -1);
			    return Boolean.TRUE;
			}
		    };
                zopdel.execute();
            } catch (InterruptedException e) {
                log.info("Caught: " + e, e);
		Thread.currentThread().interrupt();
            } catch (KeeperException.NoNodeException e) {
                // nothing
            } catch (KeeperException e) {
                log.info("Caught: " + e, e);
                throw (RuntimeException) new RuntimeException(e.getMessage()).initCause(e);
            }
            finally {
                if (callback != null) {
                    callback.lockReleased();
                }
                id = null;
            }
        }
    }

    private static class LockZooKeeperOperation implements ZooKeeperOperation {

	private void findPrefixInChildren(String prefix, ZooKeeper zookeeper, String dir) throws KeeperException, InterruptedException {
	    List<String> names = zookeeper.getChildren(dir, false);
	    for (String name : names) {
		if (name.startsWith(prefix)) {
		    id = name;
		    if (log.isInfoEnabled()) {
			log.info("Found id created last time: " + id);
		    }
		    break;
		}
	    }
	    if (id == null) {
		id = zookeeper.create(dir + "/" + prefix, data,
				      getAcl(), EPHEMERAL_SEQUENTIAL);

		if (log.isInfoEnabled()) {
		    log.info("Created id: " + id);
		}
	    }

	}

	public boolean execute() throws KeeperException, InterruptedException {
	    do {
		if (id == null) {
		    long sessionId = zookeeper.getSessionId();
		    String prefix = "x-" + sessionId + "-";
		    findPrefixInChildren(prefix, zookeeper, dir);
		    idName = new ZNodeName(id);
		}
		if (id != null) {
		    List<String> names = zookeeper.getChildren(dir, false);
		    if (names.isEmpty()) {
			log.info("No children in: " + dir + " when we've just " +
				 "created one! Lets recreate it...");
			id = null;
		    } else {
                        SortedSet<ZNodeName> sortedNames = new TreeSet<ZNodeName>();
                        for (String name : names) {
                            sortedNames.add(new ZNodeName(dir + "/" + name));
                        }
                        ownerId = sortedNames.first().getName();
                        SortedSet<ZNodeName> lessThanMe = sortedNames.headSet(idName);
                        if (!lessThanMe.isEmpty()) {
                            ZNodeName lastChildName = lessThanMe.last();
                            lastChildId = lastChildName.getName();
                            if (log.isInfoEnabled()) {
                                log.info("watching less than me node: " + lastChildId);
                            }
                            Stat stat = zookeeper.exists(lastChildId, new MyWatcher());
                            if (stat != null) {
                                return Boolean.FALSE;
                            } else {
                                log.info("Could not find the" +
					 " stats for less than me: " + lastChildName.getName());
                            }
                        } else {
                            if (isOwner()) {
                                if (callback != null) {
                                    callback.lockAcquired();
                                }
                                return Boolean.TRUE;
                            }
                        }
		    }
		}
	    }
	    while (id == null);
	    return Boolean.FALSE;
	}
    };

    public static boolean isOwner() {
        return id != null && ownerId != null && id.equals(ownerId);
    }

    protected static void retryDelay(int attemptCount) {
        if (attemptCount > 0) {
            try {
                Thread.sleep(attemptCount * retryDelay);
            } catch (InterruptedException e) {
                log.info("Failed to sleep: " + e, e);
            }
        }
    }

    static class ZNodeName implements Comparable<ZNodeName> {
	private final String name;
	private String prefix;
	private int sequence = -1;
	private final Logger log = LoggerFactory.getLogger(ZNodeName.class);

	public ZNodeName(String name) {
	    if (name == null) {
		throw new NullPointerException("id cannot be null");
	    }
	    this.name = name;
	    this.prefix = name;
	    int idx = name.lastIndexOf('-');
	    if (idx >= 0) {
		this.prefix = name.substring(0, idx);
		try {
		    this.sequence = Integer.parseInt(name.substring(idx + 1));
		} catch (NumberFormatException e) {
		    log.info("Number format exception for " + idx, e);
		} catch (ArrayIndexOutOfBoundsException e) {
		    log.info("Array out of bounds for " + idx, e);
		}
	    }
	}

	@Override
	    public String toString() {
	    return name.toString();
	}

	@Override
	    public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;

	    ZNodeName sequence = (ZNodeName) o;

	    if (!name.equals(sequence.name)) return false;

	    return true;
	}

	@Override
	    public int hashCode() {
	    return name.hashCode() + 37;
	}

	public int compareTo(ZNodeName that) {
	    int answer = this.prefix.compareTo(that.prefix);
	    if (answer == 0) {
		int s1 = this.sequence;
		int s2 = that.sequence;
		if (s1 == -1 && s2 == -1) {
		    return this.name.compareTo(that.name);
		}
		answer = s1 == -1 ? 1 : s2 == -1 ? -1 : s1 - s2;
	    }
	    return answer;
	}

	public String getName() {
	    return name;
	}

	public int getZNodeName() {
	    return sequence;
	}

	public String getPrefix() {
	    return prefix;
	}
    }

    public static List<ACL> getAcl() {
        return acl;
    }

    public static void unlockme() {
	log.info("unlockme");
	unlock();
    }

    public static void lockme() {
	log.info("lockme");
	boolean locked;
	try {
	    do {
		locked = lock();
		log.info("lockme " + locked);
	    } while (!locked);
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

}
