package roart.thread;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import roart.queue.Queues;
import roart.content.ClientHandler;

import com.vaadin.ui.UI;
import com.vaadin.ui.UIDetachedException;

public class ClientRunner implements Runnable {
	
    private static Log log = LogFactory.getLog("ClientRunner");

    public static ConcurrentMap<UI, String> uiset = new ConcurrentHashMap<UI, String>();
	
    int update = 60;
    static long lastupdate = 0;

    public void run() {
	Set<Future<Object>> set = new HashSet<Future<Object>>();
	int nThreads = 4;
    	ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
    	while (true) {
	    final long now = System.currentTimeMillis();
	    if ((now - lastupdate) >= update * 1000) {
		for (final UI ui : uiset.keySet()) {
		    try {
			ui.access(new Runnable() {
				@Override
				public void run() {
				    ((roart.client.MyVaadinUI) ui).statLabel.setValue(Queues.stat());
				}
			    });
		    } catch (UIDetachedException e) {
			log.error("UIDetachedException", e);
			uiset.remove(ui, "value");
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
		}
		lastupdate = now;
	    }
	    List<Future> removes = new ArrayList<Future>();
	    if (Queues.clientQueue.isEmpty()) {
		try {
		    TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    log.error("Exception", e);
		}
	    } else {
		Callable<Object> callable = new Callable<Object>() {
		    public Object call() /* throws Exception*/ {
			Map map = null;
			try {
			    map = ClientHandler.doClient();
			} catch (Throwable t) {
			    log.error("Exception", t);
			    //log.info("throwable");
			}
			finally {
			    //log.info("myend");
			}
			return map; //myMethod();
		    }	
		};	
 
		Future task = executorService.submit(callable);
		set.add(task);
		try {
		    TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    log.error("Exception", e);
		}
	    }

	    for (Future future : set) {
		if (future.isDone()) {
		    removes.add(future);
		    try {
			Map<UI, List> map = (Map) future.get();
			if (map == null) {
			    continue;
			}
			for (UI ui : map.keySet()) {
			    endFuture(ui, map.get(ui));
			}
		    } catch (Exception e) {
			log.error("Exception", e);
		    }
		}
	    }
	    set.removeAll(removes);
     	}
    }

    private void endFuture(final UI ui, final List<List> lists) {
	ui.access(new Runnable() {
		@Override
		public void run() {
		    ((roart.client.MyVaadinUI) ui).displayResultListsTab(lists);
		}
	    });
    }

    public static void notify(final String text) {
	for (final UI ui : uiset.keySet()) {
	    try {
		ui.access(new Runnable() {
			@Override
			public void run() {
			    ((roart.client.MyVaadinUI) ui).notify(text);
			}
		    });
	    } catch (UIDetachedException e) {
		log.error("UIDetachedException", e);
		uiset.remove(ui, "value");
	    } catch (Exception e) {
		log.error("Exception", e);
	    }
	}
    }

    // not yet
    /*
    public static void abort() {
	for (Future future : set) {
	    future.cancel(true);
	    //future.interrupt();
	}
    }
    */

}
