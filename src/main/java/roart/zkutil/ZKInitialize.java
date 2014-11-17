package roart.zkutil;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.service.ControlService;
import roart.thread.ZKRunner;
import roart.util.Constants;

public class ZKInitialize {

    static Logger log = LoggerFactory.getLogger(ZKInitialize.class);

    public static volatile ZooKeeper zk = null;

	public static void initZK(Watcher watcher) {
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
}
