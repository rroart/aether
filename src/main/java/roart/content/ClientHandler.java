package roart.content;

import com.vaadin.ui.UI;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.queue.ClientQueueElement.Function;
import roart.queue.Queues;
import roart.queue.ClientQueueElement;
import roart.util.Constants;

public class ClientHandler {
	
	private static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    public static final int timeout = 3600;
	
    public static Map<UI, List> doClient()  {
    	ClientQueueElement el = Queues.clientQueue.poll();
    	if (el == null) {
	    log.error("empty queue " + System.currentTimeMillis());
    	    return null;
    	}
    	// vulnerable spot
    	Queues.incClients();
	Function function = el.function;
	List list = null;
	if (function == Function.FILESYSTEM || function == Function.FILESYSTEMLUCENENEW || function == Function.INDEX || function == Function.REINDEXDATE || function ==  Function.REINDEXLANGUAGE) {
	    list = client(el);
	}
	if (function == Function.NOTINDEXED) {
	    list = notindexed(el);
	}
	if (function == Function.OVERLAPPING) {
	    list = overlapping();
	}
	if (function == Function.MEMORYUSAGE) {
	    list = memoryusage();
	}
	if (function == Function.SEARCH) {
	    list = search(el);
	}
	if (function == Function.SEARCHSIMILAR) {
	    list = searchsimilar(el);
	}
	if (function == Function.DBINDEX) {
	    list = dbindex(el);
	}
	if (function == Function.DBSEARCH) {
	    list = dbsearch(el);
	}
	if (function == Function.CONSISTENTCLEAN) {
	    list = consistentclean(el);
	}
	Queues.decClients();
	Map map = new HashMap<UI, List>();
	map.put(el.ui, list);
	return map;
    }

    private static List search(ClientQueueElement el) {
	roart.service.SearchService maininst = new roart.service.SearchService();
	try {
	    return maininst.searchmeDo(el);
	} catch (Exception e) {
	    return null;
	}
    }

    private static List searchsimilar(ClientQueueElement el) {
	roart.service.SearchService maininst = new roart.service.SearchService();
	try {
	    return maininst.searchsimilarDo(el);
	} catch (Exception e) {
	    return null;
	}
    }

    private static List client(ClientQueueElement el) {
	roart.service.ControlService maininst = new roart.service.ControlService();
	try {
	    return maininst.clientDo(el);
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	    return null;
	}
    }

    private static List notindexed(ClientQueueElement el) {
	roart.service.ControlService maininst = new roart.service.ControlService();
	try {
	    return maininst.notindexedDo(el);
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	    return null;
	}
    }

    private static List overlapping() {
	roart.service.ControlService maininst = new roart.service.ControlService();
	try {
	    return maininst.overlappingDo();
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	    return null;
	}
    }

    private static List memoryusage() {
	roart.service.ControlService maininst = new roart.service.ControlService();
	try {
	    return maininst.memoryusageDo();
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	    return null;
	}
    }

    private static List dbindex(ClientQueueElement el) {
	roart.service.ControlService maininst = new roart.service.ControlService();
	try {
	    return maininst.dbindexDo(el);
	} catch (Exception e) {
	    return null;
	}
    }

    private static List dbsearch(ClientQueueElement el) {
	roart.service.ControlService maininst = new roart.service.ControlService();
	try {
	    return maininst.dbsearchDo(el);
	} catch (Exception e) {
	    return null;
	}
    }

    private static List consistentclean(ClientQueueElement el) {
	roart.service.ControlService maininst = new roart.service.ControlService();
	try {
	    return maininst.consistentcleanDo(el);
	} catch (Exception e) {
	    return null;
	}
    }

}
