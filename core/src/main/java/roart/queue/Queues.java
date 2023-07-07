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

import roart.common.collections.MySet;
import roart.common.constants.Constants;
import roart.model.MyAtomicLong;
import roart.model.MyAtomicLongs;
import roart.model.MyQueue;
import roart.model.MyQueues;

/**
 * @author roart
 *
 * Event queues
 * 
 * First traverse
 * then tika
 * or other
 * index
 * 
 */

public class Queues {
	
	private static Logger log = LoggerFactory.getLogger(Queues.class);
	
    static final int limit = 100;

    //public static Queue<TikaQueueElement> tikaRunQueue = new ConcurrentLinkedQueue<TikaQueueElement>();

    public static volatile Deque<ConvertQueueElement> convertQueue = new ConcurrentLinkedDeque<ConvertQueueElement>();
    public static volatile Queue<IndexQueueElement> indexQueue = new ConcurrentLinkedQueue<IndexQueueElement>();
    public static volatile Queue<TraverseQueueElement> traverseQueue = new ConcurrentLinkedQueue<TraverseQueueElement>();

    public static Set<MySet> workQueues = new HashSet();
    
    public static volatile Queue<String> convertTimeoutQueue = new ConcurrentLinkedQueue<String>();
    
    private static volatile AtomicInteger converts = new AtomicInteger(0);
    private static volatile AtomicInteger indexs = new AtomicInteger(0);
    private static volatile AtomicInteger clients = new AtomicInteger(0);
    private static AtomicInteger traverses = new AtomicInteger(0);

    public static int getConverts() {
        return converts.get();
    }
    
    public static int getIndexs() {
    	return indexs.get();
    }
    
    public static int getClients() {
    	return clients.get();
    }
    
    public static int getTraverses() {
        return traverses.get();
    }
    
    public static void incConverts() {
        converts.incrementAndGet();
    }
    
   public static void incIndexs() {
    	indexs.incrementAndGet();
    }
    
   public static void incClients() {
       clients.incrementAndGet();
   }

   public static void incTraverses() {
       traverses.incrementAndGet();
   }
   
   public static void decConverts() {
       converts.decrementAndGet();
  }
     
  public static void decIndexs() {
   	indexs.decrementAndGet();
   }
   
   public static void decClients() {
   	clients.decrementAndGet();
   }
   
   public static void decTraverses() {
       traverses.decrementAndGet();
  }
  
    public static void resetConverts() {
        converts = new AtomicInteger(0);
    }
    
    public static void resetIndexs() {
    	indexs = new AtomicInteger(0);
    }
    
    public static void resetClients() {
    	clients = new AtomicInteger(0);
    }
    
    public static void resetTraverses() {
        traverses = new AtomicInteger(0);
    }
    
    public static boolean convertQueueHeavyLoaded() {
        return convertQueue.size() >= limit;
    }
    
    public static boolean indexQueueHeavyLoaded() {
	return indexQueue.size() >= limit;
    }
    
    public static boolean traverseQueueHeavyLoaded() {
	return traverseQueue.size() >= limit;
    }
    
   public static String webstat() {
       String queueid = Constants.TRAVERSEQUEUE;
       MyQueue<TraverseQueueElement> traverseQueue = MyQueues.get(queueid);
       return "q " + total() + " f " + traverses + " " + work() + "\nc " + convertQueue.size() + " " + converts + "\ni " + indexQueue.size() + " " + indexs;
    }

   public static String stat() {
       String queueid = Constants.TRAVERSEQUEUE;
       MyQueue<TraverseQueueElement> traverseQueue = MyQueues.get(queueid);
       return "q " + total() + " f " + traverses + " " + work() + " c " + convertQueue.size() + " " + converts + " i " + indexQueue.size() + " " + indexs;
    }

   public static void queueStat() {
       log.info("Queues {}", stat());
    }

    public static int queueSize() {
        String queueid = Constants.TRAVERSEQUEUE;
        MyQueue<TraverseQueueElement> traverseQueue = MyQueues.get(queueid);
    	return traverseQueue.size() + convertQueue.size() + indexQueue.size();
    }
    
    public static int runSize() {
        String queueid = Constants.TRAVERSEQUEUE;
        MyQueue<TraverseQueueElement> traverseQueue = MyQueues.get(queueid);
    	return converts.get() + indexs.get();
    }
    
    public static void resetConvertTimeoutQueue() {
        convertTimeoutQueue = new ConcurrentLinkedQueue<String>();
    }
    
    public static long total() {
        MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
        return total.get();
    }     
        
    public static int work() {
        int ret = 0;
        for (MySet set : workQueues) {
            ret += set.size();
        }
        return ret;
    }
    
    public static MyQueue<TraverseQueueElement> getTraverseQueue() {
        String queueid = Constants.TRAVERSEQUEUE;
        MyQueue<TraverseQueueElement> queue = MyQueues.get(queueid);
        return queue;
    }
}
