package roart.service;

import roart.queue.ClientQueueElement;
import roart.queue.ClientQueueElement.Function;
import roart.queue.Queues;
import roart.search.SearchDao;

import javax.servlet.http.*;

import java.util.Vector;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import roart.config.ConfigConstants;
import roart.config.MyConfig;
//import roart.dao.FilesDao;
import roart.database.IndexFilesDao;
import roart.model.ResultItem;
import roart.model.SearchDisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.UI;

public class SearchService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public void searchme(String str, String type) {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.SEARCH, str, type, null, null, false, false, false); // stupid overloading
	Queues.clientQueue.add(e);
    }

    public List<List> searchmeDo(ClientQueueElement e) {
	String str = e.file;
	String type = e.suffix;
	List strlist = new ArrayList<String>();

	SearchDisplay display = e.display;

	ResultItem[] strarr = roart.search.Search.searchme(str, type, display);

	for (ResultItem stri : strarr) {
	    strlist.add(stri);
	}
	List<List> strlistlist = new ArrayList<List>();
	strlistlist.add(strlist);
	return strlistlist;
    }

    public List<List> searchsimilarDo(ClientQueueElement e) {
	String str = e.file;
	String type = e.suffix;
	List strlist = new ArrayList<String>();

	SearchDisplay display = e.display;

	ResultItem[] strarr = roart.search.Search.searchsimilar(str, type, display);

	for (ResultItem stri : strarr) {
	    strlist.add(stri);
	}
	List<List> strlistlist = new ArrayList<List>();
	strlistlist.add(strlist);
	return strlistlist;
    }

	public static SearchDisplay getSearchDisplay(UI ui) {
		SearchDisplay display = new SearchDisplay();
		String myclassify = MyConfig.conf.classify;
		display.classify = myclassify != null && myclassify.length() > 0;
		// if lost session, it doesn't really matter much whether displaying for admin?
		if (ui != null && ui.getSession() != null) {
		display.admindisplay = "admin".equals((String) ui.getSession().getAttribute("user"));
		}
		display.highlightmlt = isHighlightMLT();
		return display;
	}

	public static boolean isHighlightMLT() {
		
		return MyConfig.conf.highlightmlt;
	}
    
    public void searchsimilar(String md5) {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.SEARCHSIMILAR, md5, null, null, null, false, false, false); // stupid overloading
	Queues.clientQueue.add(e);
    }
}
