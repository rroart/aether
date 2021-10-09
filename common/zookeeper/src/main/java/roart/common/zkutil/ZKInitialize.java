package roart.common.zkutil;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;

public class ZKInitialize {

    static Logger log = LoggerFactory.getLogger(ZKInitialize.class);

    public static volatile ZooKeeper zk = null;

	public synchronized static void initZK(String zookeeper, Watcher watcher, String nodename) {
	    if (zk != null) {
		return;
	    }
	    try {
		zk = new ZooKeeper(zookeeper, Integer.MAX_VALUE, watcher);
		createIfNotExists(zk, "/" + Constants.AETHER);
		createIfNotExists(zk, "/" + Constants.AETHER + "/" + Constants.LOCK);
		createIfNotExists(zk, "/" + Constants.AETHER + "/" + Constants.NODES);
		createTempIfNotExists(zk, "/" + Constants.AETHER + "/" + Constants.NODES + "/" + nodename);
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
	
        public static void createIfNotExists(ZooKeeper zk, String path) {
        try {
            Stat s = zk.exists(path, false);
            if (s == null) {
                zk.create(path, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        }
        
}
