package roart.servlet.listeners;

import roart.jpa.HDFS;
import roart.lang.LanguageDetect;

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
	
	new HDFS();
	new roart.jpa.LocalFileSystemJpa();
	String myindex = roart.util.Prop.getProp().getProperty("index");
	if (myindex.equals("solr")) {
	    new roart.jpa.SearchSolr();
	}
	String mydb = roart.util.Prop.getProp().getProperty("db");
	if (mydb.equals("hbase")) {
	    new roart.model.HbaseIndexFiles();
	} else {
		roart.service.ControlService.nodename = "localhost"; // force this
	}
	String myclassify = roart.util.Prop.getProp().getProperty("classify");
	if (myclassify != null && myclassify.equals("mahout")) {
	    new roart.jpa.MahoutClassify();
	}
	if (myclassify != null && myclassify.equals("opennlp")) {
	    new roart.jpa.OpennlpClassify();
	}
	roart.dao.FileSystemDao.instance("");
	roart.dao.SearchDao.instance(myindex);
	roart.dao.IndexFilesDao.instance(mydb);
	roart.dao.ClassifyDao.instance(myclassify);

	try {
	    LanguageDetect.init("./profiles/");
	} catch (Exception e) {
	    log.error("Exception", e);
	}

	/*
        roart.jpa.SearchLucene.indexme("cd");
        roart.jpa.SearchLucene.indexme("dvd");
        roart.jpa.SearchLucene.indexme("book");
        roart.jpa.SearchLucene.indexme("booku");
        roart.jpa.SearchLucene.indexme("book0");
	*/

	roart.service.ControlService maininst = new roart.service.ControlService();
	maininst.startThreads();

	System.out.println("config done");
	log.info("config done");
    }

    public void contextDestroyed(ServletContextEvent context) {
    }

}

