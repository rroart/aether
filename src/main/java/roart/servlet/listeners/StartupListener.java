package roart.servlet.listeners;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.*;

public class StartupListener implements javax.servlet.ServletContextListener {

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
	roart.dao.SearchDao.instance(myindex);
	roart.dao.IndexFilesDao.instance(mydb);
	System.out.println("config parsed");
    }

    public void contextDestroyed(ServletContextEvent context) {
    }

}

