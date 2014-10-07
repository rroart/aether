package roart.content;

import com.vaadin.ui.UI;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import roart.dir.Traverse;
import roart.model.IndexFiles;
import roart.model.ResultItem;
import roart.queue.Queues;
import roart.queue.ClientQueueElement;
import roart.util.ExecCommand;

import org.apache.tika.metadata.Metadata;

public class ClientHandler {
	
	private static Log log = LogFactory.getLog("ClientHandler");

    static public int timeout = 3600;
	
    public static Map<UI, List> doClient()  {
    	ClientQueueElement el = Queues.clientQueue.poll();
    	if (el == null) {
	    log.error("empty queue " + System.currentTimeMillis());
    	    return null;
    	}
    	// vulnerable spot
    	Queues.incClients();
	String function = el.function;
	List list = null;
	if (function.equals("filesystem") || function.equals("filesystemlucenenew") || function.equals("index") || function.equals("reindexdate")) {
	    list = client(el);
	}
	if (function.equals("notindexed")) {
	    list = notindexed();
	}
	if (function.equals("overlapping")) {
	    list = overlapping();
	}
	if (function.equals("memoryusage")) {
	    list = memoryusage();
	}
	Queues.decClients();
	Map map = new HashMap<UI, List>();
	map.put(el.ui, list);
	return map;
    }

    private static List client(ClientQueueElement el) {
	roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
	try {
	    return maininst.clientDo(el);
	} catch (Exception e) {
	    return null;
	}
    }

    private static List notindexed() {
	roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
	try {
	    return maininst.notindexedDo();
	} catch (Exception e) {
	    return null;
	}
    }

    private static List overlapping() {
	roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
	try {
	    return maininst.overlappingDo();
	} catch (Exception e) {
	    return null;
	}
    }

    private static List memoryusage() {
	roart.beans.session.control.Main maininst = new roart.beans.session.control.Main();
	try {
	    return maininst.memoryusageDo();
	} catch (Exception e) {
	    return null;
	}
    }

}
