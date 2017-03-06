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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.dir.Traverse;
import roart.model.IndexFiles;
import roart.model.ResultItem;
import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.util.Constants;
import roart.util.ExecCommand;
import roart.filesystem.FileSystemDao;
import roart.database.IndexFilesDao;

import org.apache.tika.metadata.Metadata;

public class OtherHandler {
	
	private static Logger log = LoggerFactory.getLogger(OtherHandler.class);

    public static volatile int timeout = 3600;
	
    public static void doOther()  {
    	TikaQueueElement el = Queues.otherQueue.poll();
    	if (el == null) {
    		log.error("empty queue");
    	    return;
    	}
    	// vulnerable spot
    	Queues.incOthers();
    	long now = System.currentTimeMillis();
    	
    	String dbfilename = el.dbfilename;
    	String filename = el.filename;
    	String md5 = el.md5;
    	IndexFiles index = el.index;
    	String retlistid = el.retlistid;
    	String retlistnotid = el.retlistnotid;
	Metadata metadata = el.metadata;

	if (filename.startsWith(FileSystemDao.FILE)) {
	    filename = filename.substring(5);
	}

	String output = null;
	boolean retry = false;
	String lowercase = filename.toLowerCase();
	File temp = null;
	String tmp = null;
    try {
	temp = File.createTempFile("other", ".txt");
    tmp = temp.getAbsolutePath();
    } catch (Exception e) {
     log.error(Constants.EXCEPTION, e);
     Queues.decOthers();
     return;
    }
    String mimetype = el.mimetype;
	// epub 2nd try
    // ebook-convert tries all, no fully good mime software
    // (lowercase.endsWith(".mobi") || lowercase.endsWith(".pdb") || lowercase.endsWith(".epub") || lowercase.endsWith(".lit") || lowercase.endsWith(".djvu") || lowercase.endsWith(".djv") || lowercase.endsWith(".dj") || lowercase.endsWith(".chm") || lowercase.endsWith(".docx"))
    if (true || mimetype.equals("application/x-mobipocket-ebook") || mimetype.equals("application/x-aportisdoc") || mimetype.equals("application/epub+zip") || mimetype.equals("image/vnd.djvu") || mimetype.equals("application/vnd.ms-htmlhelp") || mimetype.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
	    File file = new File(filename);
	    String dirname = file.getParent();
	    File dir = new File(dirname);
	    boolean w = dir.canWrite();
	    if (!w) {
		dir.setWritable(true);
	    }
	    long execstart = System.currentTimeMillis();
	    String[] arg = { filename, tmp };
	    output = executeTimeout("/usr/bin/ebook-convert", arg, retlistid, el);
	    if (output != null) {
		el.convertsw = "calibre";
		long time = execstart - System.currentTimeMillis();
		el.index.setConverttime(time);
        } else {
            log.info("ebook-convert no output");
        }
	    if (!w) {
		dir.setWritable(false);
	    }
	    retry = true;
	}
	// djvu 2nd try in case djvu not ebook-convert supported
	//	if (output != null && output.contains("ValueError: No plugin to handle input format: dj")) {
	if ((output == null || output.isEmpty()) && ((mimetype != null && mimetype.equals("image/vnd.djvu")) || (mimetype == null && lowercase.contains(".dj")))) {
	    log.info("doing2 djvutxt " + filename);
	    long execstart = System.currentTimeMillis();
	    String[] arg = { filename, tmp };
	    output = executeTimeout("/usr/bin/djvutxt", arg, retlistid, el);
	    if (output != null && !output.isEmpty()) {
		el.convertsw = "djvutxt";
		long time = execstart - System.currentTimeMillis();
		el.index.setConverttime(time);
        } else {
            log.info("djvutxt no output");
        }
	    retry = true;
	}
	// pdf 2nd try
    if ((output == null || output.isEmpty()) && coveredType(mimetype, "application/pdf", lowercase, ".pdf")) {
	    log.info("doing2 pdftotext " + filename);
	    long execstart = System.currentTimeMillis();
	    String[] arg = { filename, tmp };
	    output = executeTimeout("/usr/bin/pdftotext", arg, retlistid, el);
	    if (output != null && !output.isEmpty()) {
		el.convertsw = "pdftotext";
		long time = execstart - System.currentTimeMillis();
		el.index.setConverttime(time);
    } else {
        log.info("pdftotext no output");
    }
	    retry = true;
	}
    // doc try
	if ((output == null || output.isEmpty()) && coveredType(mimetype, "application/msword", lowercase, ".doc")) {
        log.info("doing2 wvText " + filename);
        long execstart = System.currentTimeMillis();
        String[] arg = { filename, tmp };
        output = executeTimeout("/usr/bin/wvText", arg, retlistid, el);
        if (output != null && !output.isEmpty()) {
        el.convertsw = "wvtext";
        long time = execstart - System.currentTimeMillis();
        el.index.setConverttime(time);
        } else {
            log.info("wvtext no output");
        }
        retry = true;
    }
	File txt = temp;
	long time = System.currentTimeMillis() - now;
	log.info("timerStop " + dbfilename + " " + time);
	if ((output != null && !output.isEmpty()) && retry && txt.exists()) {
		log.info("handling filename " + dbfilename + " : " + time);
		//retlist.add(new ResultItem("other handling filename " + dbfilename + " : " + time));
		TikaQueueElement e = new TikaQueueElement(filename, tmp, md5, index, retlistid, retlistnotid, metadata, el.display);
		e.convertsw = el.convertsw;
	    Queues.tikaQueue.addFirst(e);
	    //size = doTika(filename, tmp, md5, index, retlist);
	} else {
		log.info("handled not " + dbfilename + " : " + time);
		Boolean isIndexed = index.getIndexed();
		if (isIndexed == null || isIndexed.booleanValue() == false) {
		    index.incrFailed();
		    //index.save();
		}
		index.setPriority(1);
		// file unlock dbindex
		// config with finegrained distrib
		IndexFilesDao.add(index);
		log.info("delete file " + tmp);
		txt.delete();
	}
	// hdfs only
	if (filename.startsWith("/tmp/hdfs") /*|| filename.startsWith(FileSystemDao.FILE + "/tmp/other")*/) {
	    File toDel = new File(filename);
	    log.info("delete file " + filename);
	    toDel.delete();
	}
	if (false && txt.exists()) {
		 txt.delete();
	}
	Queues.decOthers();
	
    }
    
    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

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
    
    public static String executeTimeout(String filename, String [] arg, String retlistid, TikaQueueElement el) {
    	   class OtherTimeout implements Runnable {
    	    	public void run() {
    	    		try {
    	    			executeQueue();
    	    		} catch (Exception e) {
    	    			log.error(Constants.EXCEPTION, e);
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
    	    	long start = System.currentTimeMillis();
    	    	boolean b = true;
    	    	while (b) {
    	    		try {
    					Thread.sleep(1000);
    				} catch (InterruptedException e) {
    					log.error(Constants.EXCEPTION, e);
    					// TODO Auto-generated catch block
    				}
    	    		long now = System.currentTimeMillis();
    	    		if ((now - start) > 1000 * timeout) {
    	    			b = false;
    	    		}
    	    		if (!otherWorker.isAlive()) {
    	    			log.info("Otherworker finished " + filename + " " + arg[0] + " " + otherWorker + " " + otherRunnable);
    	    			return "end";
    	    		}
    	    	}
    	    	otherWorker.stop(); // .interrupt();
		el.index.setTimeoutreason(el.index.getTimeoutreason() + "othertimeout" + filename + " " + timeout + " ");
    			log.info("Otherworker timeout " + otherWorker + " " + otherRunnable);
    			try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				log.error(Constants.EXCEPTION, e);
    				// TODO Auto-generated catch block
    			}
    			log.info("Otherworker timeout " + otherWorker + " " + otherRunnable + " " + otherWorker.isAlive() + " " + otherWorker.isInterrupted() + " " + otherWorker.interrupted());

    			log.error("timeout running " + filename + " " + arg[0]);
			//retlist.add(new ResultItem("timeout running " + filename + " " + arg[0]));
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
            // ok, wait for n seconds max
            result = task.get(timeout, TimeUnit.SECONDS);
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
    
    private static boolean coveredType(String mimetype, String mimecompare, String lowercase, String lowercasecompare) {
        if (mimetype != null) {
            return mimetype.equals(mimecompare); 
        } else {
            return lowercase.endsWith(lowercasecompare);        
        }
    }

}
