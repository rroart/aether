package roart.zkutil;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.config.MyConfig;
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
		zk = new ZooKeeper(MyConfig.conf.zookeeper, Integer.MAX_VALUE, watcher);
		createTempIfNotExists(zk, "/" + Constants.AETHER);
		createTempIfNotExists(zk, "/" + Constants.AETHER + "/" + Constants.LOCK);
		createTempIfNotExists(zk, "/" + Constants.AETHER + "/" + Constants.NODES);
		createTempIfNotExists(zk, "/" + Constants.AETHER + "/" + Constants.NODES + "/" + ControlService.nodename);
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	}
	
	public static void createTempIfNotExists(ZooKeeper zk, String path) {
        try {
            Stat s = zk.exists(path, false);
            if (s == null) {
                zk.create(path, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
	}
	
}
