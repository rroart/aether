package roart.common.zk.thread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;
import roart.common.constants.FileSystemConstants;
import roart.common.zkutil.ZKInitialize;

public class ConfigThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final String path = "/" + Constants.AETHER + "/" + Constants.CONFIG + "/" + UUID.randomUUID();

    private String data;

    private ZooKeeper zk;

    private Map map;
    
    public ConfigThread(String zookeeper, int port, Map map) {
        try {
            data = InetAddress.getLocalHost().getHostName() + ":" + port;
        } catch (UnknownHostException e) {
            log.error(Constants.EXCEPTION, e);
        }
        ZKInitialize.initZK(zookeeper, new DummyWatcher(), "localhost");
        zk = ZKInitialize.zk;
        this.map = map;
    }

    @Override
    public void run() {
        while (true) {
            boolean success = false;
            while (!success) {
                boolean exists = false;
                try {
                    exists = ZKInitialize.createIfNotExists(zk, path, data.getBytes());
                    success = true;
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
                try {
                    if (true) {
                        Thread.sleep(10 * 1000);
                    }
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            }
        }
    }

    public class DummyWatcher implements Watcher {

        public void process(WatchedEvent event) {
            log.info("Process " + event.getPath() + " state " + event.getState() + " type " + event.getType());
            log.info("dummy watcher");
        }
    }

}
