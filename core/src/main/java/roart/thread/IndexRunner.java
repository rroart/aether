package roart.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.queue.QueueElement;
import roart.common.synchronization.MySemaphore;
import roart.common.synchronization.impl.MySemaphoreFactory;
import roart.common.util.TimeUtil;
import roart.common.hcutil.GetHazelcastInstance;
import roart.queue.Queues;
import roart.search.Search;
import roart.service.ControlService;
import roart.util.TraverseUtil;

public class IndexRunner implements Runnable {

    private static Logger log = LoggerFactory.getLogger(IndexRunner.class);

    public static volatile int timeout = 3600;

    private NodeConfig nodeConf;

    private ControlService controlService;

    public IndexRunner(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public void run() {
        int nThreads = ControlRunner.getThreads();
        nThreads = nodeConf.getMPThreadsIndex();
        int running = 0;
        log.info("nthreads {}", nThreads);

        for(int i = running; i < nThreads; i++) {
            Runnable run = () -> {
                Queue<MySemaphore> semaphores = new ConcurrentLinkedQueue<>();
                while (true) {
                    try {
                        unlockSemaphores(semaphores);
                        if (new Queues(nodeConf, controlService).getIndexQueueSize() == 0) {
                            log.debug("Index queue empty, sleeping");
                            TimeUtil.sleep(10);
                            continue;
                        }
                        new Queues(nodeConf, controlService).incIndexs();
                        doIndexTimeout(semaphores);
                        new Queues(nodeConf, controlService).decIndexs();
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e); 
                        TimeUtil.sleep(10);
                    }
                }
            };      
            new Thread(run).start();
        }
        try {
            TimeUnit.DAYS.sleep(1000);
            return;
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e); 
        }                   
    }

    public String doIndexTimeout(Queue<MySemaphore> semaphores) {
        class IndexTimeout implements Runnable {
            private QueueElement el;
            IndexTimeout(QueueElement el) {
                this.el = el;
            }

            public void run() {
                try {
                    TraverseUtil.doCounters(el, 1, nodeConf, controlService);
                    if (nodeConf.wantAsync()) {
                        new Search(nodeConf, controlService).indexmeQueue(el);
                    } else {
                        new Search(nodeConf, controlService).indexme(el);
                    }
                    TraverseUtil.doCounters(el, -1, nodeConf, controlService);
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            }
        }

        MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getIndexQueue();
        QueueElement el = null;
        try {
            el = queue.poll(QueueElement.class);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e); 
        }
        if (el == null) {
            log.error("empty queue");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e);
            }
            return null;
        }
        try {
            if (!nodeConf.wantAsync()) {
                MySemaphore lock = MySemaphoreFactory.create(el.getMd5(), nodeConf.getLocker(), controlService.curatorClient, GetHazelcastInstance.instance(nodeConf));
                boolean locked = lock.tryLock();
                if (!locked) {
                    queue.offer(el);
                    return null;
                }
                el.getIndexFiles().getLock().setSemaphorelock(lock);
                el.getIndexFiles().getLock().setSemaphorelockqueue(semaphores);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            queue.offer(el);
            return null;
        }
        IndexTimeout indexRunnable = new IndexTimeout(el);
        Thread indexWorker = new Thread(indexRunnable);
        indexWorker.setName("IndexTimeout");
        indexWorker.run();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e); 
        }                   
        return null;
    }

    private void unlockSemaphores(Queue<MySemaphore> locks) {
        if (!locks.isEmpty()) {
            log.info("unlock");
        }
        while (!locks.isEmpty()) {
            MySemaphore lock = locks.poll();
            lock.unlock();
            if (locks.isEmpty()) {
                log.info("unlock");
            }
        }
    }

}
