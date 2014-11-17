package roart.thread;

import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.util.Constants;
import roart.zkutil.ZKInitialize;
import roart.zkutil.ZKMessageUtil;
import roart.service.ControlService;

public class ZKRunner implements Runnable {
	
    static Logger log = LoggerFactory.getLogger(ZKRunner.class);

    public void run() {

    	List<String> children = null;

    	ZKInitialize.initZK(new DummyWatcher());
    	String dir = "/" + Constants.AETHER + "/" + Constants.NODES + "/" + ControlService.nodename;

    	while (true) {
    		log.info("get children");
    		ZKMessageUtil.MessageWatcher msgwatcher = new ZKMessageUtil.MessageWatcher();
    		try {
    			children = ZKMessageUtil.getChildren(dir, msgwatcher);
    		} catch (Exception e) {
    			log.error(Constants.EXCEPTION, e);
    		}
    		try {
    			if (children.size() == 0) {
    				msgwatcher.await();
    				continue;
    			} else {
    				ZKMessageUtil.readMsg(dir, children);
    			}
    		} catch (Exception e) {
    			log.error(Constants.EXCEPTION, e);
    		}
     	}
    }

    public class DummyWatcher implements Watcher {

   	public void process(WatchedEvent event) {
   		log.info("dummy watcher");
   	}
    	}
}
