package roart.queue;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Queues {
	
	private static Log log = LogFactory.getLog("Queues");
	
    public static Queue<TikaQueueElement> tikaQueue = new ConcurrentLinkedQueue<TikaQueueElement>();
    public static Queue<TikaQueueElement> otherQueue = new ConcurrentLinkedQueue<TikaQueueElement>();
    public static Queue<IndexQueueElement> indexQueue = new ConcurrentLinkedQueue<IndexQueueElement>();
    
    private static int tikas = 0;
    private static int others = 0;
    private static int indexs = 0;
    
    public static synchronized void incTikas() {
    	tikas++;
    }
    
    public static synchronized void incOthers() {
    	others++;
    }
    
   public static synchronized void incIndexs() {
    	indexs++;
    }
    
   public static synchronized void decTikas() {
   	tikas--;
   }
   
   public static synchronized void decOthers() {
   	others--;
   }
   
  public static synchronized void decIndexs() {
   	indexs--;
   }
   
   public static void queueStat() {
    	log.info("Queues t " + tikaQueue.size() + " " + tikas + " o " + otherQueue.size() + " " + others + " i " + indexQueue.size() + " " + indexs);
    }

    public static int queueSize() {
    	return tikaQueue.size() + otherQueue.size() + indexQueue.size();
    }
    
    public static synchronized int runSize() {
    	return tikas + others + indexs;
    }
}