package roart.servlet.listeners;

import roart.filesystem.HDFS;
import roart.lang.LanguageDetect;
import roart.util.ConfigConstants;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartupListener implements javax.servlet.ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(StartupListener.class);

    public void contextInitialized(ServletContextEvent context)  {
	roart.service.ControlService.parseconfig();
	
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
	}

	roart.service.ControlService maininst = new roart.service.ControlService();
	maininst.startThreads();

	System.out.println("config done");
	log.info("config done");
    }

    public void contextDestroyed(ServletContextEvent context) {
    }

}

