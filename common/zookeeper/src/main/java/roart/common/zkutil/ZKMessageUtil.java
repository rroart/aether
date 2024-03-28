package roart.common.zkutil;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
//import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;

public class ZKMessageUtil {

    static Logger log = LoggerFactory.getLogger(ZKMessageUtil.class);

    public static List<String> getChildren(String dir, Watcher watcher) throws KeeperException, InterruptedException {
	    try {
	        //Stat i = ZKInitialize.zk.exists(null, false);
	        return ZKInitialize.zk.getChildren(dir, watcher);
	    } catch (KeeperException.NoNodeException e){
	        throw e;
	    }
	}

	public static void dorefresh(String nodename) {
	if (true || ZKInitialize.zk == null) {
	    return;
	}
	log.info("sendMsgRefresh");
	try {
	    List<String> nodes = ZKInitialize.zk.getChildren(ZKUtil.getPath() + Constants.NODES, false);
	    for (String node : nodes) {
		if (!nodename.equals(node)) {
			ZKInitialize.createTempIfNotExists(ZKInitialize.zk, ZKUtil.getPath(Constants.NODES) + node + "/" + Constants.REFRESH);
		    log.info("send refresh to " + node);
		}
	    }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
	}

    public static void doreconfig(String nodename) {
    if (ZKInitialize.zk == null) {
        return;
    }
    log.info("sendMsgReconfig");
    try {
        List<String> nodes = ZKInitialize.zk.getChildren(ZKUtil.getPath() + Constants.NODES, false);
        for (String node : nodes) {
        if (!nodename.equals(node)) {
            ZKInitialize.createTempIfNotExists(ZKInitialize.zk, ZKUtil.getPath(Constants.NODES) + node + "/" + Constants.RECONFIG);
            log.info("send reconfig to " + node);
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
