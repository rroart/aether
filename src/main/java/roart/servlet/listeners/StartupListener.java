package roart.servlet.listeners;

import roart.filesystem.HDFS;
import roart.hcutil.GetHazelcastInstance;
import roart.lang.LanguageDetect;
import roart.model.ResultItem;
import roart.queue.TikaQueueElement;
import roart.service.ControlService;
import roart.util.ConfigConstants;
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
	roart.service.ControlService.parseconfig();
	
	ControlService.configMap.put(ControlService.Config.FAILEDLIMIT, ConfigConstants.DEFAULT_CONFIG_FAILEDLIMIT);
	ControlService.configMap.put(ControlService.Config.TIKATIMEOUT, ConfigConstants.DEFAULT_CONFIG_TIKATIMEOUT);
	ControlService.configMap.put(ControlService.Config.OTHERTIMEOUT, ConfigConstants.DEFAULT_CONFIG_OTHERTIMEOUT);
	ControlService.configMap.put(ControlService.Config.INDEXLIMIT, ConfigConstants.DEFAULT_CONFIG_INDEXLIMIT);
	ControlService.configMap.put(ControlService.Config.REINDEXLIMIT, ConfigConstants.DEFAULT_CONFIG_REINDEXLIMIT);

	// solr defaults
	ControlService.configMap.put(ControlService.Config.MLTCOUNT, ConfigConstants.DEFAULT_CONFIG_MLTCOUNT);
    ControlService.configMap.put(ControlService.Config.MLTMINDF, ConfigConstants.DEFAULT_CONFIG_MLTMINDF);
    ControlService.configMap.put(ControlService.Config.MLTMINTF, ConfigConstants.DEFAULT_CONFIG_MLTMINTF);
	
	ControlService.configStrMap.put(ControlService.Config.FAILEDLIMIT, ConfigConstants.FAILEDLIMIT);
	ControlService.configStrMap.put(ControlService.Config.TIKATIMEOUT, ConfigConstants.TIKATIMEOUT);
	ControlService.configStrMap.put(ControlService.Config.OTHERTIMEOUT, ConfigConstants.OTHERTIMEOUT);
	ControlService.configStrMap.put(ControlService.Config.INDEXLIMIT, ConfigConstants.INDEXLIMIT);
	ControlService.configStrMap.put(ControlService.Config.REINDEXLIMIT, ConfigConstants.REINDEXLIMIT);

    ControlService.configStrMap.put(ControlService.Config.MLTCOUNT, ConfigConstants.MLTCOUNT);
    ControlService.configStrMap.put(ControlService.Config.MLTMINDF, ConfigConstants.MLTMINDF);
    ControlService.configStrMap.put(ControlService.Config.MLTMINTF, ConfigConstants.MLTMINTF);
    
	for (ControlService.Config conf : ControlService.configMap.keySet()) {
		String str = Prop.getProp().getProperty(ControlService.configStrMap.get(conf));
		if (str != null) {
			int value = getInteger(str);
			if (value >= 0) {
				ControlService.configMap.put(conf, value);
			}
		}
	}
	
	String myfs = roart.util.Prop.getProp().getProperty(ConfigConstants.FS);
	if (myfs == null) {
	    myfs = ConfigConstants.LOCAL;
	}
	new HDFS();
	new roart.filesystem.LocalFileSystemAccess();
	String myindex = roart.util.Prop.getProp().getProperty(ConfigConstants.INDEX);
	if (myindex.equals(ConfigConstants.SOLR)) {
	    new roart.search.SearchSolr();
	}
	if (myindex.equals(ConfigConstants.LUCENE)) {
	    org.apache.lucene.search.BooleanQuery.setMaxClauseCount(16384);
	}
	String mydb = roart.util.Prop.getProp().getProperty(ConfigConstants.DB);
	if (mydb.equals(ConfigConstants.HBASE)) {
	    new roart.database.HbaseIndexFiles();
	} else if (mydb.equals(ConfigConstants.DATANUCLEUS)) {
		new roart.database.DataNucleusIndexFiles();
	}
	if (mydb.equals(ConfigConstants.HIBERNATE) || myindex.equals(ConfigConstants.LUCENE)) {
		roart.service.ControlService.nodename = ConfigConstants.LOCALHOST; // force this
	}
	String myclassify = roart.util.Prop.getProp().getProperty(ConfigConstants.CLASSIFY);
	if (myclassify != null && myclassify.equals(ConfigConstants.MAHOUT)) {
	    new roart.classification.MahoutClassify();
	}
	if (myclassify != null && myclassify.equals(ConfigConstants.OPENNLP)) {
	    new roart.classification.OpennlpClassify();
	}
	roart.filesystem.FileSystemDao.instance(myfs);
	roart.search.SearchDao.instance(myindex);
	roart.database.IndexFilesDao.instance(mydb);
	roart.classification.ClassifyDao.instance(myclassify);

    try {
	    LanguageDetect.init("./profiles/");
	} catch (Exception e) {
	    log.error("Exception", e);
	}

	String myzoo = roart.util.Prop.getProp().getProperty(ConfigConstants.ZOOKEEPER);
	if (myzoo != null && !roart.service.ControlService.nodename.equals(ConfigConstants.LOCALHOST)) {
	    roart.service.ControlService.zookeeper = myzoo;
        ControlService.locker = roart.util.Constants.CURATOR;
	}

    String zookeepermode = roart.util.Prop.getProp().getProperty(ConfigConstants.DISTRIBUTEDLOCKMODE);
    if (zookeepermode != null && zookeepermode.equals(ConfigConstants.SMALL)) {
        roart.service.ControlService.zookeepersmall = true;
    } else {
        roart.service.ControlService.zookeepersmall = false;
    }

    String distributedtraverse = roart.util.Prop.getProp().getProperty(ConfigConstants.DISTRIBUTEDPROCESS);
    if (distributedtraverse != null && distributedtraverse.equals("true")) {
        roart.service.ControlService.distributedtraverse = true;
        GetHazelcastInstance.instance();
        ControlService.locker = roart.util.Constants.HAZELCAST;
        MyCollections.remover = new MyHazelcastRemover();
    } else {
        roart.service.ControlService.distributedtraverse = false;
   }

    if (roart.util.Constants.CURATOR.equals(ControlService.locker)) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);        
        String zookeeperConnectionString = myzoo;
        ControlService.curatorClient = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        ControlService.curatorClient.start();
    }
    
    //ControlService.lock = MyLockFactory.create();
    
	roart.service.ControlService maininst = new roart.service.ControlService();
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

