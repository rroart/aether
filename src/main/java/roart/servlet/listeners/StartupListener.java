package roart.servlet.listeners;

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
	roart.beans.session.control.Main.parseconfig();
	String myindex = roart.util.Prop.getProp().getProperty("myindex");
	if (myindex.equals("solr")) {
	    new roart.jpa.SearchSolr();
	}
	String mydb = roart.util.Prop.getProp().getProperty("mydb");
	if (mydb.equals("hbase")) {
	    new roart.model.HbaseIndexFiles();
	}
	String myclassify = roart.util.Prop.getProp().getProperty("myclassify");
	if (myclassify.equals("mahout")) {
	    new roart.jpa.MahoutClassify();
	}
	if (myclassify.equals("opennlp")) {
	    new roart.jpa.OpennlpClassify();
	}
	roart.dao.SearchDao.instance(myindex);
	roart.dao.IndexFilesDao.instance(mydb);
	roart.dao.ClassifyDao.instance(myclassify);

        roart.jpa.SearchLucene.indexme("cd");
        roart.jpa.SearchLucene.indexme("dvd");
        roart.jpa.SearchLucene.indexme("book");
        roart.jpa.SearchLucene.indexme("booku");
        roart.jpa.SearchLucene.indexme("book0");

	roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
	maininst.startThreads();

	System.out.println("config done");
	log.info("config done");
    }

    public void contextDestroyed(ServletContextEvent context) {
    }

}

