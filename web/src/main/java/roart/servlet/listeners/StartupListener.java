package roart.servlet.listeners;

import roart.config.ConfigConstants;
import roart.config.MyConfig;
import roart.config.MyPropertyConfig;
import roart.filesystem.HDFS;
import roart.hcutil.GetHazelcastInstance;
import roart.lang.LanguageDetect;
import roart.model.ResultItem;
import roart.queue.TikaQueueElement;
import roart.service.ControlService;
import roart.util.EurekaUtil;
import roart.util.MyCollections;
import roart.util.MyHazelcastQueue;
import roart.util.MyHazelcastRemover;
import roart.util.MyJavaQueue;
import roart.util.MyLockFactory;
import roart.util.Prop;
import sun.rmi.rmic.newrmic.Constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartupListener implements javax.servlet.ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(StartupListener.class);

    public void contextInitialized(ServletContextEvent context)  {
	    
    	EurekaUtil.initEurekaClient();
    	
	MyConfig conf = MyPropertyConfig.instance();
	conf.config();
	
    //ControlService.lock = MyLockFactory.create();
    
	ControlService maininst = new ControlService();
	maininst.startThreads();

	System.out.println("config done");
	log.info("config done");
    }

    private Integer getInteger(String str) {
    	try {
    		return new Integer(str);
    	} catch (NumberFormatException e) {
    		log.error(Constants.EXCEPTION, e);
    	}
		return -1;
	}

	public void contextDestroyed(ServletContextEvent context) {
    }

}

