package roart.zkutil;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;

import roart.database.IndexFilesDao;
import roart.service.ControlService;
import roart.thread.ClientRunner;
import roart.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKMessageUtil {

    static Logger log = LoggerFactory.getLogger(ZKLockUtil.class);

    public static List<String> getChildren(String dir, Watcher watcher) throws KeeperException, InterruptedException {
	    try {
	        return ZKInitialize.zk.getChildren(dir, watcher);
	    } catch (KeeperException.NoNodeException e){
	        throw e;
	    }
	}

	public static void readMsg(String dir, List<String> children) {
	try {
	    for (String child : children) {
		if (child.equals(Constants.REFRESH)) {
		    IndexFilesDao.getAll();
		    log.info(Constants.REFRESH + " " + ControlService.nodename);
		    ClientRunner.notify("Finished refresh");
		} else {
		    log.info("unknown command " + child);
		}
		ZKInitialize.zk.delete(dir + "/" + child, 0);
	    }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
	}

	public static void dorefresh() {
	if (ZKInitialize.zk == null) {
	    return;
	}
	log.info("sendMsgRefresh");
	try {
	    List<String> nodes = ZKInitialize.zk.getChildren("/" + Constants.AETHER + "/" + Constants.NODES, false);
	    for (String node : nodes) {
		if (!ControlService.nodename.equals(node)) {
			ZKInitialize.zk.create("/" + Constants.AETHER + "/" + Constants.NODES + "/" + node + "/" + Constants.REFRESH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		    log.info("send refresh to " + node);
		}
	    }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
	}

public static class MessageWatcher implements Watcher {

CountDownLatch latch;

public MessageWatcher() {
    try {
	latch = new CountDownLatch(1);
    } catch (Exception e) {
	log.error(Constants.EXCEPTION, e);
    }
}

public void process(WatchedEvent event) {
    log.info("Process " + event.getPath() + " state " + event.getState() + " type " + event.getType());
    if (event.getPath() == null) {
	return;
    }
    latch.countDown();
}

    public void await() throws InterruptedException {
        latch.await();
    }

}

}
