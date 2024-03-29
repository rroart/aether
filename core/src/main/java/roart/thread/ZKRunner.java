package roart.thread;

import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.zkutil.ZKInitialize;
import roart.common.zkutil.ZKMessageUtil;
import roart.common.zkutil.ZKUtil;
import roart.database.IndexFilesDao;
import roart.service.ControlService;

public class ZKRunner implements Runnable {
	
    static Logger log = LoggerFactory.getLogger(ZKRunner.class);

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public ZKRunner(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public void run() {

    	List<String> children = null;

    	ZKInitialize.initZK(nodeConf.getZookeeper(), new DummyWatcher(), controlService.nodename);
    	String dir = ZKUtil.getAppidPath(Constants.NODES) + controlService.nodename;

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
    				readMsg(dir, children);
    			}
    		} catch (Exception e) {
    			log.error(Constants.EXCEPTION, e);
    		}
     	}
    }

    public class DummyWatcher implements Watcher {

   	public void process(WatchedEvent event) {
   		log.info("Process " + event.getPath() + " state " + event.getState() + " type " + event.getType());
   		log.info("dummy watcher");
   	}
    	}
    
    public void readMsg(String dir, List<String> children) {
    try {
        for (String child : children) {
        if (child.equals(Constants.REFRESH)) {
            new IndexFilesDao(nodeConf, controlService).getAll();
            log.info(Constants.REFRESH + " " + controlService.nodename);
            //ClientRunner.notify("Finished refresh");
        } else if (child.equals(Constants.RECONFIG)) {
            // TODO fix
                //MyConfig.instance().reconfig();
                log.info(Constants.RECONFIG + " " + controlService.nodename);
            //ClientRunner.replace();
                //ClientRunner.notify("Finished reconfig");
        } else {
            log.info("unknown command " + child);
        }
        ZKInitialize.zk.delete(dir + "/" + child, 0);
        }
    } catch (Exception e) {
        log.error(Constants.EXCEPTION, e);
    }
    }


}
