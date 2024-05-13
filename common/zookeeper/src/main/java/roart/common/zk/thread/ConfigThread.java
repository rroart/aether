package roart.common.zk.thread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;
import roart.common.zkutil.ZKInitialize;
import roart.common.zkutil.ZKUtil;

public class ConfigThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final String path = ZKUtil.getAppidPath(Constants.CONFIG) + UUID.randomUUID();

    private String data;

    private ZooKeeper zk;

    Function<Queue<String>, Integer> handleConfig;
    //public ConfigThread(String zookeeper, int port, boolean useHostName, Function<Queue, Integer> f) {
   // }

    public ConfigThread(String zookeeper, int port, boolean useHostName, Function<Queue<String>, Integer> handleConfig)  {
        try {
            if (useHostName) {
                data = InetAddress.getLocalHost().getHostName() + ":" + port;
            } else {
                data = InetAddress.getLocalHost().getHostAddress() + ":" + port;
            }
        } catch (UnknownHostException e) {
            log.error(Constants.EXCEPTION, e);
        }
        ZKInitialize.initZK(zookeeper, new DummyWatcher(), "localhost");
        zk = ZKInitialize.zk;
        this.handleConfig = handleConfig;
    }

    @Override
    public void run() {
        while (true) {
            boolean success = false;
            while (!success) {
                boolean exists = false;
                try {
                    //exists = ZKInitialize.createIfNotExists(zk, path, data.getBytes());
                    //success = true;
                    String zAppidPath = ZKUtil.getAppidPath() + Constants.CONFIG;
                    List<String> children = zk.getChildren(zAppidPath, true);
                    Queue<String> params = new ConcurrentLinkedQueue<>();
                    for (String child : children) {
                        byte[] bytes = zk.getData(zAppidPath + "/" + child, false, null);
                        params.add(new String(bytes));
                    }
                    handleConfig.apply(params);
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

    public void runold() {
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
