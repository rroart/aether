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
            createIfNotExists(zk, ZKUtil.getCommonPath0());
            createIfNotExists(zk, ZKUtil.getCommonPath() + Constants.CONFIG);
            createIfNotExists(zk, ZKUtil.getCommonPath() + Constants.DATA);
            createIfNotExists(zk, ZKUtil.getCommonPath() + Constants.QUEUES);
            createIfNotExists(zk, ZKUtil.getAppidPath0());
            createIfNotExists(zk, ZKUtil.getAppidPath() + Constants.CONFIG);
            createIfNotExists(zk, ZKUtil.getAppidPath() + Constants.DATA);
            createIfNotExists(zk, ZKUtil.getAppidPath() + Constants.DB);
            createIfNotExists(zk, ZKUtil.getAppidPath() + Constants.FS);
            createIfNotExists(zk, ZKUtil.getAppidPath() + Constants.LATCH);
            createIfNotExists(zk, ZKUtil.getAppidPath() + Constants.LOCK);
            createIfNotExists(zk, ZKUtil.getAppidPath() + Constants.NODES);
            createIfNotExists(zk, ZKUtil.getAppidPath() + Constants.QUEUES);
            createTempIfNotExists(zk, ZKUtil.getAppidPath(Constants.NODES) + nodename);
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

    public static boolean createIfNotExists(ZooKeeper zk, String path) {
        return createIfNotExists(zk, path, new byte[0]);
    }

    public static boolean createIfNotExists(ZooKeeper zk, String path, byte[] data) {
        try {
            Stat s = zk.exists(path, false);
            if (s == null) {
                zk.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            return s != null;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            return false;
        }
    }

}
