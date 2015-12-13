package roart.queue;

import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.util.Constants;
import roart.util.MyQueue;
import roart.util.MyQueues;

public class Queues {
	
	private static Logger log = LoggerFactory.getLogger(Queues.class);
	
    static final int limit = 100;

    //public static Queue<TikaQueueElement> tikaRunQueue = new ConcurrentLinkedQueue<TikaQueueElement>();

    public static volatile Deque<TikaQueueElement> tikaQueue = new ConcurrentLinkedDeque<TikaQueueElement>();
    public static volatile Queue<TikaQueueElement> otherQueue = new ConcurrentLinkedQueue<TikaQueueElement>();
    public static volatile Queue<IndexQueueElement> indexQueue = new ConcurrentLinkedQueue<IndexQueueElement>();
    public static volatile Queue<ClientQueueElement> clientQueue = new ConcurrentLinkedQueue<ClientQueueElement>();
    public static volatile Queue<TraverseQueueElement> traverseQueue = new ConcurrentLinkedQueue<TraverseQueueElement>();

    public static volatile Queue<String> tikaTimeoutQueue = new ConcurrentLinkedQueue<String>();
    
    private static volatile AtomicInteger tikas = new AtomicInteger(0);
    private static volatile AtomicInteger others = new AtomicInteger(0);
    private static volatile AtomicInteger indexs = new AtomicInteger(0);
    private static volatile AtomicInteger clients = new AtomicInteger(0);
    
    public static int getTikas() {
    	return tikas.get();
    }
    
    public static int getIndexs() {
    	return indexs.get();
    }
    
    public static int getOthers() {
    	return others.get();
    }
    
    public static int getClients() {
    	return clients.get();
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
   
   public static void decClients() {
   	clients.decrementAndGet();
   }
   
    public static void incClients() {
    	clients.incrementAndGet();
    }

    public static void resetTikas() {
    	tikas = new AtomicInteger(0);
    }
    
    public static void resetOthers() {
    	others = new AtomicInteger(0);
    }
    
    public static void resetIndexs() {
    	indexs = new AtomicInteger(0);
    }
    
    public static void resetClients() {
    	clients = new AtomicInteger(0);
    }
    
    public static boolean tikaQueueHeavyLoaded() {
	return tikaQueue.size() >= limit;
    }
    
    public static boolean indexQueueHeavyLoaded() {
	return indexQueue.size() >= limit;
    }
    
    public static boolean otherQueueHeavyLoaded() {
	return otherQueue.size() >= limit;
    }
    
    public static boolean traverseQueueHeavyLoaded() {
    return traverseQueue.size() >= limit;
    }
    
   public static String webstat() {
       String queueid = Constants.TRAVERSEQUEUE;
       MyQueue<TraverseQueueElement> traverseQueue = MyQueues.get(queueid);
       return "f " + traverseQueue.size() + " / " + traverseQueue.size() + "\nt " + tikaQueue.size() + " / " + tikas + "\no " + otherQueue.size() + " / " + others + "\ni " + indexQueue.size() + " / " + indexs;
    }

   public static String stat() {
       String queueid = Constants.TRAVERSEQUEUE;
       MyQueue<TraverseQueueElement> traverseQueue = MyQueues.get(queueid);
       return "f " + traverseQueue.size() + " / " + traverseQueue.size() + " t " + tikaQueue.size() + " " + tikas + " o " + otherQueue.size() + " " + others + " i " + indexQueue.size() + " " + indexs;
    }

   public static void queueStat() {
       log.info("Queues " + stat());
    }

    public static int queueSize() {
        String queueid = Constants.TRAVERSEQUEUE;
        MyQueue<TraverseQueueElement> traverseQueue = MyQueues.get(queueid);
    	return traverseQueue.size() + tikaQueue.size() + otherQueue.size() + indexQueue.size();
    }
    
    public static int runSize() {
        String queueid = Constants.TRAVERSEQUEUE;
        MyQueue<TraverseQueueElement> traverseQueue = MyQueues.get(queueid);
    	return tikas.get() + others.get() + indexs.get();
    }
    
    public static void resetTikaTimeoutQueue() {
    	tikaTimeoutQueue = new ConcurrentLinkedQueue<String>();
    }
}
