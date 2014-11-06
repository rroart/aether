package roart.service;

import roart.queue.ClientQueueElement;
import roart.queue.ClientQueueElement.Function;
import roart.queue.Queues;
import roart.search.SearchDao;
import roart.util.ConfigConstants;

import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;

import java.util.ArrayList;
import java.util.List;

import java.io.*;

//import roart.dao.FilesDao;
import roart.database.IndexFilesDao;
import roart.model.ResultItem;
import roart.model.SearchDisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public void searchme(String str, String type) {
	ClientQueueElement e = new ClientQueueElement(com.vaadin.ui.UI.getCurrent(), Function.SEARCH, str, type, null, null, false, false); // stupid overloading
	Queues.clientQueue.add(e);
    }

    public List<List> searchmeDo(ClientQueueElement e) {
	String str = e.file;
	String type = e.suffix;
	List strlist = new ArrayList<String>();

	SearchDisplay display = new SearchDisplay();
	String myclassify = roart.util.Prop.getProp().getProperty(ConfigConstants.CLASSIFY);
	display.classify = myclassify != null && myclassify.length() > 0;
	display.admindisplay = "admin".equals((String) e.ui.getSession().getAttribute("user"));

	ResultItem[] strarr = roart.search.Search.searchme(str, type, display);

	for (ResultItem stri : strarr) {
	    strlist.add(stri);
	}
	List<List> strlistlist = new ArrayList<List>();
	strlistlist.add(strlist);
	return strlistlist;
    }
    
    public List<ResultItem> searchsimilar(String md5) {
	List strlist = new ArrayList<String>();
	ResultItem[] strarr = roart.search.Search.searchsimilar(md5);
	if (strarr == null) {
	    return strlist;
	}
	for (ResultItem stri : strarr) {
	    strlist.add(stri);
	}
	return strlist;
    }
}
