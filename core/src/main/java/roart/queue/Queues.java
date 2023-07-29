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

import roart.common.collections.MyQueue;
import roart.common.collections.MySet;
import roart.common.collections.impl.MyAtomicLong;
import roart.common.collections.impl.MyAtomicLongs;
import roart.common.collections.impl.MyQueues;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.hcutil.GetHazelcastInstance;
import roart.service.ControlService;

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

    private Logger log = LoggerFactory.getLogger(Queues.class);

    final int limit = 100;

    //public Queue<TikaQueueElement> tikaRunQueue = new ConcurrentLinkedQueue<TikaQueueElement>();

    /*
    public volatile Deque<ConvertQueueElement> convertQueue = new ConcurrentLinkedDeque<ConvertQueueElement>();
    public volatile Queue<IndexQueueElement> indexQueue = new ConcurrentLinkedQueue<IndexQueueElement>();
    public volatile Queue<TraverseQueueElement>  getMy = new ConcurrentLinkedQueue<TraverseQueueElement>();
    public volatile Queue<ListQueueElement> listQueue = new ConcurrentLinkedQueue<>();
*/
    
    public Set<Set> workQueues = new HashSet();

    public volatile Queue<String> convertTimeoutQueue = new ConcurrentLinkedQueue<String>();

    /*
    private volatile AtomicInteger converts = new AtomicInteger(0);
    private volatile AtomicInteger indexs = new AtomicInteger(0);
    private volatile AtomicInteger clients = new AtomicInteger(0);
    private AtomicInteger traverses = new AtomicInteger(0);
    private AtomicInteger listings = new AtomicInteger(0);
*/
    private NodeConfig nodeConf;

    private ControlService controlService;    

    public Queues(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public long getConverts() {
        return getMyConverts().get();
    }

    public long getIndexs() {
        return getMyIndexs().get();
    }

    public long getClients() {
        return getMyClients().get();
    }

    public long getTraverses() {
        return getMyTraverses().get();
    }

    public long getListings() {
        return getMyListings().get();
    }

    public void incConverts() {
        getMyConverts().incrementAndGet();
    }

    public void incIndexs() {
        getMyIndexs().incrementAndGet();
    }

    public void incClients() {
        getMyClients().incrementAndGet();
    }

    public void incTraverses() {
        getMyTraverses().incrementAndGet();
    }

    public void incListings() {
        getMyListings().incrementAndGet();
    }

    public void decConverts() {
        getMyConverts().decrementAndGet();
    }

    public void decIndexs() {
        getMyIndexs().decrementAndGet();
    }

    public void decClients() {
        getMyClients().decrementAndGet();
    }

    public void decTraverses() {
        getMyTraverses().decrementAndGet();
    }

    public void decListings() {
        getMyListings().decrementAndGet();
    }

    public void resetConverts() {
        getMyConverts().set(0);
    }

    public void resetIndexs() {
        getMyIndexs().set(0);
    }

    public void resetClients() {
        getMyClients().set(0);
    }

    public void resetTraverses() {
        getMyTraverses().set(0);
    }

    public void resetListings() {
        getMyListings().set(0);
    }

    public boolean convertQueueHeavyLoaded() {
        return getConvertQueueSize() >= limit;
    }

    public boolean indexQueueHeavyLoaded() {
        return getIndexQueueSize() >= limit;
    }

    public boolean traverseQueueHeavyLoaded() {
        return getTraverseQueueSize() >= limit;
    }

    public boolean listingQueueHeavyLoaded() {
        return getListingQueueSize() >= limit;
    }

    public String webstat() {
        return "q " + total() + " l " + getListings() + " \nf " + getTraverses() + " " + work() + "\nc " + getConvertQueueSize() + " " + getConverts() + "\ni " + getIndexQueueSize() + " " + getIndexs();
    }

    public String stat() {
        return "q " + total() + " l " + getListings() + " f " + getTraverses() + " " + work() + " c " + getConvertQueueSize() + " " + getConverts() + " i " + getIndexQueueSize() + " " + getIndexs();
    }

    public void queueStat() {
        log.info("Queues {}", stat());
    }

    public long queueSize() {
        return getListingQueueSize() + getTraverseQueueSize() + getConvertQueueSize() + getIndexQueueSize();
    }

    public long runSize() {
        return getMyConverts().get() + getMyIndexs().get();
    }

    public void resetConvertTimeoutQueue() {
        convertTimeoutQueue = new ConcurrentLinkedQueue<String>();
    }

    public long total() {
        MyAtomicLong total = MyAtomicLongs.get(prefix() + Constants.TRAVERSECOUNT, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
        return total.get();
    }     

    public int work() {
        int ret = 0;
        for (Set set : workQueues) {
            ret += set.size();
        }
        return ret;
    }

    public MyQueue<ListQueueElement> getListingQueue() {
        String queueid = prefix() + Constants.LISTINGQUEUE;
        MyQueue<ListQueueElement> queue = MyQueues.get(queueid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
        return queue;
    }

    public MyQueue<TraverseQueueElement> getTraverseQueue() {
        String queueid = prefix() + Constants.TRAVERSEQUEUE;
        MyQueue<TraverseQueueElement> queue = MyQueues.get(queueid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
        return queue;
    }

    public MyQueue<ConvertQueueElement> getConvertQueue() {
        String queueid = prefix() + Constants.CONVERTQUEUE;
        MyQueue<ConvertQueueElement> queue = MyQueues.get(queueid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
        return queue;
    }

    public MyQueue<IndexQueueElement> getIndexQueue() {
        String queueid = prefix() + Constants.INDEXQUEUE;
        MyQueue<IndexQueueElement> queue = MyQueues.get(queueid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
        return queue;
    }
    
    public MyAtomicLong getMyConverts() {
        return MyAtomicLongs.get(prefix() + Constants.CONVERTS, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
    }

    public MyAtomicLong getMyIndexs() {
        return MyAtomicLongs.get(prefix() + Constants.INDEXS, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
    }

    public MyAtomicLong getMyTraverses() {
        return MyAtomicLongs.get(prefix() + Constants.TRAVERSES, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
    }

    public MyAtomicLong getMyListings() {
        return MyAtomicLongs.get(prefix() + Constants.LISTINGS, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
    }

    public MyAtomicLong getMyClients() {
        return MyAtomicLongs.get(prefix() + Constants.CLIENTS, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
    }

    public int getListingQueueSize() {
        return getListingQueue().size();
        //return MyAtomicLongs.get(Constants.LISTINGQUEUESIZE);
    }

    public int getTraverseQueueSize() {
        return getTraverseQueue().size();
        //return MyAtomicLongs.get(Constants.TRAVERSEQUEUESIZE);
    }

    public int getConvertQueueSize() {
        return getConvertQueue().size();
        //return MyAtomicLongs.get(Constants.CONVERTQUEUESIZE);
    }

    public int getIndexQueueSize() {
        return getIndexQueue().size();
        //return MyAtomicLongs.get(Constants.INDEXQUEUESIZE);
    }

    public String prefix() {
        String appid = System.getenv(Constants.APPID);
        if (appid != null) {
            return appid;
        } else {
            return "";
        }
    }
}
