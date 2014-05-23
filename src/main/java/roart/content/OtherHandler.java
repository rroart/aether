package roart.content;

import java.io.File;
import java.util.Date;
import java.util.List;
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
import roart.model.Index;
import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.util.ExecCommand;

import org.apache.tika.metadata.Metadata;

public class OtherHandler {
	
	private static Log log = LogFactory.getLog("OtherHandler");
	
    public static void doOther()  {
    	TikaQueueElement el = Queues.otherQueue.poll();
    	if (el == null) {
    		log.error("empty queue");
    	    return;
    	}
    	// vulnerable spot
    	Queues.incOthers();
    	long now = new Date().getTime();
    	
    	String dbfilename = el.dbfilename;
    	String filename = el.filename;
    	String md5 = el.md5;
    	Index index = el.index;
    	List<String> retlist = el.retlist;
	Metadata metadata = el.metadata;

	String output = null;
	boolean retry = false;
	String lowercase = filename.toLowerCase();
	File temp = null;
	String tmp = null;
    try {
	temp = File.createTempFile("other", ".txt");
    tmp = temp.getAbsolutePath();
    } catch (Exception e) {
     log.error("Exception", e);
    }
	// epub 2nd try
	if (lowercase.endsWith(".mobi") || lowercase.endsWith(".pdb") || lowercase.endsWith(".epub") || lowercase.endsWith(".lit") || lowercase.endsWith(".djvu") || lowercase.endsWith(".djv") || lowercase.endsWith(".dj") || lowercase.endsWith(".chm")) {
	    File file = new File(filename);
	    String dirname = file.getParent();
	    File dir = new File(dirname);
	    boolean w = dir.canWrite();
	    if (!w) {
		dir.setWritable(true);
	    }
	    String[] arg = { filename, tmp };
	    output = executeTimeout("/usr/bin/ebook-convert", arg, retlist);
	    if (output != null) {
		el.convertsw = "calibre";
	    }
	    if (!w) {
		dir.setWritable(false);
	    }
	    retry = true;
	}
	// djvu 2nd try in case djvu not ebook-convert supported
	//	if (output != null && output.contains("ValueError: No plugin to handle input format: dj")) {
	if (output == null && lowercase.contains(".dj")) {
	    log.info("doing2 djvutxt");
	    String[] arg = { filename, tmp };
	    output = executeTimeout("/usr/bin/djvutxt", arg, retlist);
	    if (output != null) {
		el.convertsw = "djvutxt";
	    }
	    retry = true;
	}
	// pdf 2nd try
	if (output == null && lowercase.endsWith(".pdf")) {
	    log.info("doing2 pdftotext");
	    String[] arg = { filename, tmp };
	    output = executeTimeout("/usr/bin/pdftotext", arg, retlist);
	    if (output != null) {
		el.convertsw = "pdftotext";
	    }
	    retry = true;
	}
	File txt = temp;
	long time = new Date().getTime() - now;
	log.info("timerStop " + dbfilename + " " + time);
	if (output != null && retry && txt.exists()) {
		log.info("handling filename " + dbfilename + " : " + time);
		retlist.add("other handling filename " + dbfilename + " : " + time);
		TikaQueueElement e = new TikaQueueElement(filename, tmp, md5, index, retlist, metadata);
		e.convertsw = el.convertsw;
	    Queues.tikaQueue.add(e);
	    //size = doTika(filename, tmp, md5, index, retlist);
	} else {
		log.info("handled not " + dbfilename + " : " + time);
		Boolean isIndexed = index.getIndexed();
		if (isIndexed == null || isIndexed.booleanValue() == false) {
		    index.incrFailed();
		}
	}
	if (false && txt.exists()) {
		 txt.delete();
	}
	Queues.decOthers();
	
    }
    
    private static java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static String execute(String filename, String[] arg) {
	String res = null;

	ExecCommand ec = new ExecCommand();
	ec.execute(filename, arg);

	res = ec.getOutput() + ec.getError();
	log.info("output " + res);
	return res;
    }
    
    public static Object executeQueue() {
    	Object [] objs = execQueue.poll();
    	String filename = (String) objs[0];
    	String [] arg = (String []) objs[1];
    	return execute(filename, arg);
    }
    
    public static String executeTimeout(String filename, String [] arg, List<String> retlist) {
    	   class OtherTimeout implements Runnable {
    	    	public void run() {
    	    		try {
    	    			executeQueue();
    	    		} catch (Exception e) {
    	    			log.error("Exception", e);
    	    		}
    	    	}
    	    }
    	    
	    	Object [] objs = new Object[2];
	    	objs[0] = filename;
	    	objs[1] = arg;
	    	execQueue.add(objs);

	    	OtherTimeout otherRunnable = new OtherTimeout();
    	    	Thread otherWorker = new Thread(otherRunnable);
    	    	otherWorker.setName("OtherTimeout");
    	    	otherWorker.start();
    	    	long start = new Date().getTime();
    	    	boolean b = true;
    	    	while (b) {
    	    		try {
    					Thread.sleep(1000);
    				} catch (InterruptedException e) {
    					log.error("Exception", e);
    					// TODO Auto-generated catch block
    				}
    	    		long now = new Date().getTime();
    	    		if ((now - start) > 1000 * 60 * 10) {
    	    			b = false;
    	    		}
    	    		if (!otherWorker.isAlive()) {
    	    			log.info("Otherworker finished " + filename + " " + arg[0] + " " + otherWorker + " " + otherRunnable);
    	    			return "end";
    	    		}
    	    	}
    	    	otherWorker.stop(); // .interrupt();
    			log.info("Otherworker timeout " + otherWorker + " " + otherRunnable);
    			try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				log.error("Exception", e);
    				// TODO Auto-generated catch block
    			}
    			log.info("Otherworker timeout " + otherWorker + " " + otherRunnable + " " + otherWorker.isAlive() + " " + otherWorker.isInterrupted() + " " + otherWorker.interrupted());

    			log.error("timeout running " + filename + " " + arg[0]);
            retlist.add("timeout running " + filename + " " + arg[0]);
        return (String) null;
    }

    public static String executeTimeout2(String filename, String [] arg, List<String> retlist) {
    	Object [] objs = new Object[2];
    	objs[0] = filename;
    	objs[1] = arg;
    	execQueue.add(objs);
        Callable<Object> callable = new Callable<Object>() {
            public Object call() throws Exception {
                return executeQueue();
            }
        };
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<Object> task = executorService.submit(callable);
        Object result = null;
        try {
            // ok, wait for 600 seconds max
            result = task.get(600, TimeUnit.SECONDS);
            log.info("Finished with result: " + result);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            log.error("timeout running " + filename + " " + arg[0]);
            retlist.add("timeout running " + filename + " " + arg[0]);
        } catch (InterruptedException e) {
            log.error("interrupted", e);
        }
        List list = executorService.shutdownNow();
        log.error("Shutdown now list size " + list.size());
        return (String) result;
    }

}