package roart.queue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Queues {
	
	private static Log log = LogFactory.getLog("Queues");
	
    //public static Queue<TikaQueueElement> tikaRunQueue = new ConcurrentLinkedQueue<TikaQueueElement>();

    public static Queue<TikaQueueElement> tikaQueue = new ConcurrentLinkedQueue<TikaQueueElement>();
    public static Queue<TikaQueueElement> otherQueue = new ConcurrentLinkedQueue<TikaQueueElement>();
    public static Queue<IndexQueueElement> indexQueue = new ConcurrentLinkedQueue<IndexQueueElement>();

    public static Queue<String> tikaTimeoutQueue = new ConcurrentLinkedQueue<String>();
    
    private static AtomicInteger tikas = new AtomicInteger(0);
    private static AtomicInteger others = new AtomicInteger(0);
    private static AtomicInteger indexs = new AtomicInteger(0);
    
    public static int getTikas() {
    	return tikas.get();
    }
    
    public static void incTikas() {
    	tikas.incrementAndGet();
    }
    
    public static void incOthers() {
    	others.incrementAndGet();
    }
    
   public static void incIndexs() {
    	indexs.incrementAndGet();
    }
    
   public static void decTikas() {
   	tikas.decrementAndGet();
   }
   
   public static void decOthers() {
   	others.decrementAndGet();
   }
   
  public static void decIndexs() {
   	indexs.decrementAndGet();
   }
   
   public static void queueStat() {
    	log.info("Queues t " + tikaQueue.size() + " " + tikas + " o " + otherQueue.size() + " " + others + " i " + indexQueue.size() + " " + indexs);
    }

    public static int queueSize() {
    	return tikaQueue.size() + otherQueue.size() + indexQueue.size();
    }
    
    public static int runSize() {
    	return tikas.get() + others.get() + indexs.get();
    }
    
    public static void resetTikaTimeoutQueue() {
    	tikaTimeoutQueue = new ConcurrentLinkedQueue<String>();
    }
}