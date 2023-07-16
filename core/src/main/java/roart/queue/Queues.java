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

    /*
    public static volatile Deque<ConvertQueueElement> convertQueue = new ConcurrentLinkedDeque<ConvertQueueElement>();
    public static volatile Queue<IndexQueueElement> indexQueue = new ConcurrentLinkedQueue<IndexQueueElement>();
    public static volatile Queue<TraverseQueueElement>  getMy = new ConcurrentLinkedQueue<TraverseQueueElement>();
    public static volatile Queue<ListQueueElement> listQueue = new ConcurrentLinkedQueue<>();
*/
    
    public static Set<MySet> workQueues = new HashSet();

    public static volatile Queue<String> convertTimeoutQueue = new ConcurrentLinkedQueue<String>();

    /*
    private static volatile AtomicInteger converts = new AtomicInteger(0);
    private static volatile AtomicInteger indexs = new AtomicInteger(0);
    private static volatile AtomicInteger clients = new AtomicInteger(0);
    private static AtomicInteger traverses = new AtomicInteger(0);
    private static AtomicInteger listings = new AtomicInteger(0);
*/

    public static long getConverts() {
        return getMyConverts().get();
    }

    public static long getIndexs() {
        return getMyIndexs().get();
    }

    public static long getClients() {
        return getMyClients().get();
    }

    public static long getTraverses() {
        return getMyTraverses().get();
    }

    public static long getListings() {
        return getMyListings().get();
    }

    public static void incConverts() {
        getMyConverts().incrementAndGet();
    }

    public static void incIndexs() {
        getMyIndexs().incrementAndGet();
    }

    public static void incClients() {
        getMyClients().incrementAndGet();
    }

    public static void incTraverses() {
        getMyTraverses().incrementAndGet();
    }

    public static void incListings() {
        getMyListings().incrementAndGet();
    }

    public static void decConverts() {
        getMyConverts().decrementAndGet();
    }

    public static void decIndexs() {
        getMyIndexs().decrementAndGet();
    }

    public static void decClients() {
        getMyClients().decrementAndGet();
    }

    public static void decTraverses() {
        getMyTraverses().decrementAndGet();
    }

    public static void decListings() {
        getMyListings().decrementAndGet();
    }

    public static void resetConverts() {
        getMyConverts().set(0);
    }

    public static void resetIndexs() {
        getMyIndexs().set(0);
    }

    public static void resetClients() {
        getMyClients().set(0);
    }

    public static void resetTraverses() {
        getMyTraverses().set(0);
    }

    public static void resetListings() {
        getMyListings().set(0);
    }

    public static boolean convertQueueHeavyLoaded() {
        return getConvertQueue().size() >= limit;
    }

    public static boolean indexQueueHeavyLoaded() {
        return getIndexQueue().size() >= limit;
    }

    public static boolean traverseQueueHeavyLoaded() {
        return getTraverseQueue().size() >= limit;
    }

    public static boolean listingQueueHeavyLoaded() {
        return getListingQueue().size() >= limit;
    }

    public static String webstat() {
        return "q " + total() + " l " + getListings() + " \nf " + getTraverses() + " " + work() + "\nc " + getConvertQueue().size() + " " + getConverts() + "\ni " + getIndexQueue().size() + " " + getIndexs();
    }

    public static String stat() {
        return "q " + total() + " l " + getListings() + " f " + getTraverses() + " " + work() + " c " + getConvertQueue().size() + " " + getConverts() + " i " + getIndexQueue().size() + " " + getIndexs();
    }

    public static void queueStat() {
        log.info("Queues {}", stat());
    }

    public static int queueSize() {
        return getListingQueue().size() + getTraverseQueue().size() + getConvertQueue().size() + getIndexQueue().size();
    }

    public static long runSize() {
        return getMyConverts().get() + getMyIndexs().get();
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

    public static MyQueue<ListQueueElement> getListingQueue() {
        String queueid = Constants.LISTINGQUEUE;
        MyQueue<ListQueueElement> queue = MyQueues.get(queueid);
        return queue;
    }

    public static MyQueue<TraverseQueueElement> getTraverseQueue() {
        String queueid = Constants.TRAVERSEQUEUE;
        MyQueue<TraverseQueueElement> queue = MyQueues.get(queueid);
        return queue;
    }

    public static MyQueue<ConvertQueueElement> getConvertQueue() {
        String queueid = Constants.CONVERTQUEUE;
        MyQueue<ConvertQueueElement> queue = MyQueues.get(queueid);
        return queue;
    }

    public static MyQueue<IndexQueueElement> getIndexQueue() {
        String queueid = Constants.INDEXQUEUE;
        MyQueue<IndexQueueElement> queue = MyQueues.get(queueid);
        return queue;
    }
    
    public static MyAtomicLong getMyConverts() {
        return MyAtomicLongs.get(Constants.CONVERTS);
    }

    public static MyAtomicLong getMyIndexs() {
        return MyAtomicLongs.get(Constants.INDEXS);
    }

    public static MyAtomicLong getMyTraverses() {
        return MyAtomicLongs.get(Constants.TRAVERSES);
    }

    public static MyAtomicLong getMyListings() {
        return MyAtomicLongs.get(Constants.LISTINGS);
    }

    public static MyAtomicLong getMyClients() {
        return MyAtomicLongs.get(Constants.CLIENTS);
    }

}
